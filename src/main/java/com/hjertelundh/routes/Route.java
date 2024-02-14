package com.hjertelundh.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "routes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Route {
    @Id
    private ObjectId id;

    private String lineNumber;

    private String JourneyPatternPointNumber;

    private String DirectionCode;

}
