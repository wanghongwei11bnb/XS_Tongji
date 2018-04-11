JSON.stringify({
    settings: {
        number_of_shards: 3,
        number_of_replicas: 0
    },
    mappings: {
        house: {
            dynamic: false,
            properties: {
                id: {
                    type: "integer"
                },
                title: {
                    type: "text",
                },
                price: {
                    type: "integer"
                },
                area: {
                    type: "integer"
                },
                createTime: {
                    type: "date",
                    format: "strict_date_optional_time||epoch_millis"
                },
                lastUpdateTime: {
                    type: "date",
                    format: "strict_date_optional_time||epoch_millis"
                },
                cityEnName: {
                    type: "keyword"
                },
                tags: {
                    type: "text"
                }
            }
        }
    }
});