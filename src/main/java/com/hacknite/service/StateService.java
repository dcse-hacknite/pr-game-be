package com.hacknite.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.hacknite.model.db.PlanetInfo;
import com.hacknite.model.db.PositionInfo;
import com.hacknite.model.db.RocketInfo;
import com.hacknite.model.db.StateInfo;
import com.hacknite.model.request.*;
import com.hacknite.model.response.*;
import com.hacknite.repository.StateInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StateService {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    StateInfoRepository repository;

    private static Integer DEFAULT_SECONDS_REMAINING = 1000;

    public StateResponse handleGitRequest(GitEventRequest request) {
//        createDBIfNotExists();
        StateInfo stateInfo = determineAndStoreNewState(request);
        return createResponseFromStateInfo(stateInfo);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Iterable<StateInfo> findAll() {
        return repository.findAll();
    }

    private StateResponse createResponseFromStateInfo(StateInfo stateInfo) {
        List<PlanetResponse> planets = Arrays.asList(new PlanetResponse("Mars", 100L));

        List<RocketResponse> rockets = new ArrayList<>();
        stateInfo.getRockets()
                .forEach(it -> rockets.add(new RocketResponse(it.getId(), it.getBranchName(), it.getAuthorAvatars(), it.getSecondsRemaining(),
                        new PositionResponse(it.getPosition()
                        .getDistance(), it.getPosition().getOrder()), it.getStatus())));
        return new StateResponse(planets, rockets);
    }

    protected StateInfo determineAndStoreNewState(GitEventRequest request) {
        Iterator<StateInfo> stateInfoIter = repository.findAll().iterator();
        StateInfo stateInfo;
        if (stateInfoIter.hasNext()) {
            stateInfo = stateInfoIter.next();
            RocketInfo currentRocket = stateInfo.getRockets().stream()
                    .filter(rocketInfo -> (rocketInfo.getId() != null && rocketInfo.getId().equals(request.getDetails().getId()))).findFirst().orElse(null);

            RocketInfo rocketNewPosition = null;
            if (currentRocket != null) {
                rocketNewPosition = resolveNewRocketPosition(currentRocket, request);
            } else {
                currentRocket = createStartupRocket(request);
                rocketNewPosition = resolveNewRocketPosition(currentRocket, request);
            }
            //Replace rocket
            String currentRocketId = currentRocket.getId();
            List<RocketInfo> newRockets = stateInfo.getRockets().stream().filter(rocketInfo -> !rocketInfo.getId().equals(currentRocketId)
                    || rocketInfo.getStatus().equals(StatusType.CRASHED))
                    .collect(Collectors.toList());
            newRockets.add(rocketNewPosition);
            stateInfo.setRockets(newRockets);
            stateInfo = repository.save(stateInfo);

        } else {
            //Completely new state, nothing yet exists
            stateInfo = repository.save(createStateInfoFromRequest(request));
        }
        return stateInfo;
    }

    private StateInfo createStateInfoFromRequest(GitEventRequest request) {
        List<PlanetInfo> planets = Arrays.asList(new PlanetInfo("Mars", 100L));
        RocketInfo rocket = createStartupRocket(request);
        RocketInfo updatedPositionAndStatus = resolveNewRocketPosition(rocket, request);
        List<RocketInfo> rockets = Arrays.asList(updatedPositionAndStatus);
        StateInfo info = new StateInfo(planets, rockets);
        return info;
    }

    private RocketInfo createStartupRocket(GitEventRequest request) {
        List<String> authorAvatars = request.getDetails().getAuthors().stream().map(it -> it.getAvatarUrl()).collect(Collectors.toList());
        Double ordeInYAxis = new Random().nextDouble();
        RocketInfo rocket = new RocketInfo(request.getDetails().getId(), request.getDetails()
                .getBranchName(), authorAvatars, 1000, new PositionInfo(0D, ordeInYAxis), StatusType.AWAITING_LAUNCH);
        return rocket;
    }

    private RocketInfo resolveNewRocketPosition(RocketInfo currentRocket, GitEventRequest request) {
        PositionInfo newPosition;
        switch (request.getAction()) {
            case BRANCH_CREATED:
//                RocketResponse rocket = new RocketResponse("id", null, null, new PositionResponse(0d, 0d), StatusType.AWAITING_LAUNCH );
                break;
            case PULL_REQUEST_CREATED:
            case PULL_REQUEST_UPDATED:
                newPosition = new PositionInfo(randomBetween(0d, 0.1d), currentRocket.getPosition().getOrder());
                return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                        currentRocket.getSecondsRemaining(), newPosition, StatusType.FLYING);
            case PULL_REQUEST_REVIEWED:
                switch (request.getDetails().getOutcome()) {
                    case APPROVE:
                        PositionInfo oldPosition = currentRocket.getPosition();
                        newPosition = new PositionInfo(oldPosition.getDistance() + randomBetween(0.3d, 0.4d), currentRocket.getPosition().getOrder());
                        return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                                currentRocket.getSecondsRemaining(), newPosition, StatusType.FLYING);
                    case COMMENT:
                        return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                                currentRocket.getSecondsRemaining(), currentRocket.getPosition(), StatusType.FLYING);
                    case REQUEST_CHANGE:
                        return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                                currentRocket.getSecondsRemaining(), currentRocket.getPosition(), StatusType.MUTINY);
                }
            case PULL_REQUEST_MERGED:
                newPosition = new PositionInfo(randomBetween(1d, 1d), currentRocket.getPosition().getOrder());
                return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                        currentRocket.getSecondsRemaining(), newPosition, StatusType.LANDED);
            case PULL_REQUEST_DELETED:
                newPosition = new PositionInfo(randomBetween(0d, 0d), currentRocket.getPosition().getOrder());
                return new RocketInfo(currentRocket.getId(), currentRocket.getBranchName(), currentRocket.getAuthorAvatars(),
                        currentRocket.getSecondsRemaining(), newPosition, StatusType.CRASHED);
        }
        return currentRocket;
    }

    private void createDBIfNotExists() {
        try {
            dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(StateInfo.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
        } catch (Exception e) {
            if (e.getMessage().contains("Table already exists: StateInfo")) {
            } else {
                throw e;
            }
        }
    }

    public Double randomBetween(double min, double max) {
        Random r = new Random();
        return (r.nextInt((int) ((max - min) * 10 + 1)) + min * 10) / 10.0;
    }

    public StateResponse getCurrentState() {
        Iterator<StateInfo> stateInfoIter = repository.findAll().iterator();
        if (stateInfoIter.hasNext()) {
            return createResponseFromStateInfo(stateInfoIter.next());
        }
        return null;
    }
}
