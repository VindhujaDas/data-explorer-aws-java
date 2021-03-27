package lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Message;
import models.Status;
import models.Explorer;

import java.util.UUID;

public class InitializerLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Message message;
    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());

    public void setupWorkspace(SNSEvent event, Context context) {

        LambdaLogger logger = context.getLogger();

        logger.log("reading message from SNS and parsing it");
        event.getRecords().forEach(snsRecord -> {
            try {
                message = objectMapper.readValue(snsRecord
                                .getSNS().getMessage(),
                        Message.class);
                logger.log("received message: " + message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        if (message.getStatus().equals(Status.SUCCESS)) {

            logger.log("saving workspace details in dynamoDB");
            Table table = dynamoDB.getTable(System.getenv("WORKSPACE_TABLE"));
            Explorer explorer = new Explorer(message.getParentSite(), message.getExplorerName());
            explorer.setHost(message.getHost());
            explorer.setSiteId(message.getSiteId());

            Item item = new Item().withPrimaryKey("id", generateUniqueId())
                    .withString("parentSite", explorer.getParentSite())
                    .withString("name", explorer.getName())
                    .withString("host", explorer.getHost())
                    .withString("siteId", explorer.getSiteId());
            table.putItem(item);

            logger.log("successfully saved the workspace details in dynamoDB");
        } else {
            logger.log("received a workspace provisioning failure message in SNS: " +
                    "\nERROR: "+message.getErrorCode() +":" + message.getErrorMessage());
            throw new RuntimeException("ERROR: "+message.getErrorCode() +":" + message.getErrorMessage());
        }

    }

    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    };
}
