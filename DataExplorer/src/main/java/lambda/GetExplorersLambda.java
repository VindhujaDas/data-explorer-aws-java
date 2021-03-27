package lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Explorer;

import java.util.List;
import java.util.stream.Collectors;

public class GetExplorersLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();

    public APIGatewayProxyResponseEvent getWorkspaces(APIGatewayProxyRequestEvent request)
            throws JsonProcessingException {
        ScanResult scanResult = dynamoDB.scan(new ScanRequest().withTableName(System.getenv("WORKSPACE_TABLE")));
        List<Explorer> explorers = scanResult
                .getItems().stream().map(item -> new Explorer(Integer.parseInt(item.get("id").getN()),
                        item.get("parentSite").getS(), item.get("name").getS(), item.get("host").getS(), item.get("siteId").getS()))
                .collect(Collectors.toList());

        String jsonOutput = objectMapper.writeValueAsString(explorers);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(jsonOutput);
    }
}
