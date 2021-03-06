package com.hackathon.clari.relationshipgraph.controller;

import com.hackathon.clari.relationshipgraph.queryParam.CypherQueryParam;
import com.hackathon.clari.relationshipgraph.queryParam.DetailsQueryParam;
import com.hackathon.clari.relationshipgraph.queryParam.MagicPathQueryParam;
import com.hackathon.clari.relationshipgraph.queryParam.SearchQueryParam;
import com.hackathon.clari.relationshipgraph.queryresponse.ConnectionsResponse;
import com.hackathon.clari.relationshipgraph.queryresponse.MagicPathResponse;
import com.hackathon.clari.relationshipgraph.queryresponse.SearchResponse;
import com.hackathon.clari.relationshipgraph.queryresponse.TopConnectionsResponse;
import com.hackathon.clari.relationshipgraph.service.DetailsService;
import com.hackathon.clari.relationshipgraph.service.MagicPathService;
import com.hackathon.clari.relationshipgraph.service.SearchService;
import com.hackathon.clari.relationshipgraph.service.TopConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Author: senthamaraik kannan
 * Date: 08/12/20
 * Time: 6:06 PM
 */
@RestController
public class ConnectionController {

    @GetMapping("/top-connection")
    public TopConnectionsResponse getTopConnection(@RequestParam("isInternal") final boolean isInternal,
                                                   @RequestParam("email") final String email,
                                                   @RequestParam("limit") final int limit) {

        final CypherQueryParam cypherQueryParam = new CypherQueryParam(email.trim(), limit, isInternal);
        final TopConnectionService topConnectionService = new TopConnectionService();
        return new TopConnectionsResponse(topConnectionService.apply(cypherQueryParam));
    }

    @GetMapping("/search")
    public List<SearchResponse> getSearchedUsers(@RequestParam("email") final String email) {

        final SearchQueryParam searchQueryParam = new SearchQueryParam(email.trim());
        final SearchService searchService = new SearchService();
        return searchService.apply(searchQueryParam);
    }

    @GetMapping("/details")
    public ConnectionsResponse getDetails(@RequestParam("email") final String email,
                                          @RequestParam("isInternal") final boolean isInternal) {

        final DetailsQueryParam cypherQueryParam = new DetailsQueryParam(email.trim(), isInternal);
        final DetailsService detailsService = new DetailsService();
        return detailsService.apply(cypherQueryParam);
    }

    @GetMapping("/magic-path")
    public List<MagicPathResponse> getMagicPath(@RequestParam("email") final String email,
                                                @RequestParam("searchText") final String searchText,
                                                @RequestParam("isAccountSearch") final boolean isAccountSearch) {

        final MagicPathQueryParam magicPathQueryParam = new MagicPathQueryParam(email.trim(), searchText, isAccountSearch);
        final MagicPathService magicPathService = new MagicPathService();
        final List<MagicPathResponse> apply = magicPathService.apply(magicPathQueryParam);
        return apply.stream().limit(5).collect(Collectors.toList());
    }

    @GetMapping("/hello")
    public String getString() {

        return "hello";
    }

}
