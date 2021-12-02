package com.hacknite.model.request;

import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class GitEventDetailsRequest {
    String id;
    String name;
    Date updatedAt;
    Date createdAt;
    Date reviewedAt;
    List<AuthorRequest> authors;
    String branchName;
    AuthorRequest reviewer;
    ReviewOutcomeType outcome;
}
