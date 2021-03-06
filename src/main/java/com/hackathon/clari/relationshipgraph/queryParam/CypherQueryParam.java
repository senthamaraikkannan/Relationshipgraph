package com.hackathon.clari.relationshipgraph.queryParam;

/**
 * Author: senthamaraik kannan
 * Date: 08/12/20
 * Time: 11:48 AM
 */
public class CypherQueryParam implements  QueryParam{

    private final String email;
    private final Integer limit;
    private final Boolean isInternal;

    public CypherQueryParam(final String email,
                            final Integer limit,
                            final Boolean isInternal) {
        this.email = email;
        this.limit = limit;
        this.isInternal = isInternal;
    }

    public String getEmail() {
        return email;
    }

    public Integer getLimit() {
        return limit;
    }

    public Boolean getIsInternal() {
        return isInternal;
    }
}
