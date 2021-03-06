package com.hackathon.clari.relationshipgraph.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.clari.relationshipgraph.queryParam.CypherQueryParam;
import com.hackathon.clari.relationshipgraph.queryParam.DetailsQueryParam;
import com.hackathon.clari.relationshipgraph.queryresponse.ConnectionsResponse;
import com.hackathon.clari.relationshipgraph.queryresponse.UserAndActivityCount;
import com.hackathon.clari.relationshipgraph.queryresponse.UserAndLastEngaged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: senthamaraik kannan
 * Date: 09/12/20
 * Time: 11:47 AM
 */
public class DetailsService extends ActivityGraphService<DetailsQueryParam, ConnectionsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetailsService.class);

    public ConnectionsResponse apply(final DetailsQueryParam detailsQueryParam) {


        final List<UserAndActivityCount> internalConnections = new ActivityCountSpecificUserService().apply(detailsQueryParam);

        final List<String> topUserList = internalConnections.stream().map(UserAndActivityCount::getUserEmail).collect(Collectors.toList());

        if (topUserList == null || topUserList.isEmpty())
            return new ConnectionsResponse("", Collections.EMPTY_MAP, "", 0, null);

        final Map<String, UserAndLastEngaged> userAndLastEngagedMap = new LastEngagedDateService(topUserList).apply(new CypherQueryParam(detailsQueryParam.getEmail(), null, detailsQueryParam.getInternal()))
                .stream()
                .collect(Collectors.toMap(UserAndLastEngaged::getUserEmail, Function.identity()));


        // generate relative activity scores
        final Map<Integer, Integer> activityScoreVsPercentileMap = calculatePercentiles(internalConnections.stream().map(UserAndActivityCount::getActivityScore).collect(Collectors.toList()));

        final List<ConnectionsResponse> connectionsResponseList = new ArrayList<>();
        internalConnections.stream().forEach(userAndActivityCount -> {
            final String userEmail = userAndActivityCount.getUserEmail();
            long lastEngagedDate = 0;
            if (userAndLastEngagedMap.containsKey(userEmail)) {
                lastEngagedDate = userAndLastEngagedMap.get(userEmail).getLasEngagedDate();
            }
            int activityScore = 0;
            if (activityScoreVsPercentileMap.containsKey(userAndActivityCount.getActivityScore())) {
                activityScore = activityScoreVsPercentileMap.get(userAndActivityCount.getActivityScore());
            }

            connectionsResponseList.add(new ConnectionsResponse(userAndActivityCount.getUserName(), userAndActivityCount.getActivityCountMap(), userEmail, activityScore, lastEngagedDate));
        });


        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            LOGGER.info(" Connection for isInternal:{} : {}", detailsQueryParam.getInternal(), objectMapper.writeValueAsString(connectionsResponseList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return connectionsResponseList.get(0);
    }
}
