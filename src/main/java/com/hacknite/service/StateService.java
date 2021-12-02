package com.hacknite.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.hacknite.model.db.PlanetInfo;
import com.hacknite.model.db.PositionInfo;
import com.hacknite.model.db.RocketInfo;
import com.hacknite.model.db.StateInfo;
import com.hacknite.model.response.StatusType;
import com.hacknite.repository.StateInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StateService {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    StateInfoRepository repository;

    public void saveState() {
        try {
            dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

            createDBIfNotExists();

            List<PlanetInfo> planets = Arrays.asList(new PlanetInfo("Mars", 100L));
            List<RocketInfo> rockets = Arrays.asList(new RocketInfo(1L, Arrays.asList("link"), 60, new PositionInfo(0.2, 0.5), StatusType.FLYING));
            StateInfo info = new StateInfo(planets, rockets);

            repository.save(info);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void createDBIfNotExists() {
        try {
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(StateInfo.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
        } catch (Exception e) {
            if(e.getMessage().contains("Table already exists: StateInfo")) {
            } else {
                throw e;
            }
        }
    }

    public void fetchAll() {
        Iterable<StateInfo> states = repository.findAll();
        StateInfo info = states.iterator().next();
    }
}
