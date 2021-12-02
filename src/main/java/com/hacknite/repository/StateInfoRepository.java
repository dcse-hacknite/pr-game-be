package com.hacknite.repository;

import com.hacknite.model.db.StateInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface StateInfoRepository extends CrudRepository<StateInfo, String> {
    Optional<StateInfo> findById(String id);
}
