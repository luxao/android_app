package com.example.journey_dp.data.domain

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("query")
    val query: Query
)

data class Query(
    @SerializedName("pages")
    val pages: Map<String, Page>
)

data class Page(
    @SerializedName("extract")
    val extract: String
)
