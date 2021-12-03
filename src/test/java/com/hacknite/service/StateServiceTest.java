package com.hacknite.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.hacknite.model.db.PlanetInfo;
import com.hacknite.model.db.PositionInfo;
import com.hacknite.model.db.RocketInfo;
import com.hacknite.model.db.StateInfo;
import com.hacknite.model.response.StatusType;
import com.hacknite.repository.StateInfoRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

public class StateServiceTest {

    @Mock
    private StateInfoRepository repository;
    @Mock
    private Iterable<StateInfo> stateInfos;
    @Mock
    private Iterator<StateInfo> stateInfoIter;

    @InjectMocks
    private StateService service = new StateService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void doSomething() {
        when(repository.findAll()).thenReturn(stateInfos);
        when(stateInfos.iterator()).thenReturn(stateInfoIter);
        when(stateInfoIter.hasNext()).thenReturn(true);


        List<PlanetInfo> planets = Arrays.asList(new PlanetInfo("Mars", 100L));
        RocketInfo rocket1 = new RocketInfo("Rocket1", "branchName", Arrays.asList("AuthorAvatar1", "AuthorAvatar1-2"), 1000, new PositionInfo(0D, 0D), StatusType.AWAITING_LAUNCH);
        RocketInfo rocket2 = new RocketInfo("Rocket2", "BranchName", Arrays.asList("AuthorAvatar2", "AuthorAvatar2-2"), 1000, new PositionInfo(0D, 0D), StatusType.FLYING);
        List<RocketInfo> rockets = Arrays.asList(rocket1, rocket2);
        StateInfo stateInfo = new StateInfo(planets, rockets);
        when(stateInfoIter.next()).thenReturn(stateInfo);

        service.determineAndStoreNewState(null);

    }

    @Test
    public void testRandom() {
        Double random = service.randomBetween(0.112D, 0.583D);
    }
}
