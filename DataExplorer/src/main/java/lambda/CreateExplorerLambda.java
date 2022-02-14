package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import models.Error;


import java.util.Random;

public class CreateExplorerLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    private Response response;
    private Error error;
    private Random random = new Random();

    public APIGatewayProxyResponseEvent createWorkspace(APIGatewayProxyRequestEvent request, Context context)
            throws JsonProcessingException {
        LambdaLogger logger = context.getLogger();

        logger.log("parsing and validating the request");
        Explorer explorer = objectMapper.readValue(request.getBody(), Explorer.class);

        if (explorer.getParentSite().equals("")) {
            error = new Error(4567, "Invalid value for parent-site");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(objectMapper.writeValueAsString(error));
        }

        if (explorer.getName().equals("")) {
            error = new Error(9876, "Invalid value for workspace name");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(objectMapper.writeValueAsString(error));
        }

        logger.log("***** mocking REST call to provision QueryGrid and SQLE with query-service *****");

        // Just to simulate an error scenario for demo
        if (explorer.getId() < 0) {
            logger.log("publishing an error notification to SNS");
            publishMessageToSNS(explorer, buildErrorMessage(explorer));
        } else {
            logger.log("publishing a success notification to SNS");
            publishMessageToSNS(explorer, buildSuccessMessage(explorer));
        }
        response = new Response("Request submitted successfully for Workspace: " + explorer.getName(), "test-"+random.nextInt(1000));
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(202)
                .withBody(objectMapper.writeValueAsString(response));
    }

    private void publishMessageToSNS(Explorer explorer, Message message) {

        try {
            sns.publish(System.getenv("PROVISION_WORKSPACE_TOPIC"), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private Message buildSuccessMessage(Explorer explorer) {
        Message message = new Message();
        message.setStatus(Status.SUCCESS);
        message.setParentSite(explorer.getParentSite());
        message.setDefaultUser("vc"+random.nextInt(Integer.MAX_VALUE));
        message.setHost(random.nextInt(255)+"."+random.nextInt(255)+"."+random.nextInt(10)+"."+random.nextInt(10));
        message.setSiteId("test-"+ random.nextInt(1000));
        message.setExplorerName(explorer.getName());
        return message;

    }

    private Message buildErrorMessage(Explorer explorer) {
        Message message = new Message();
        message.setStatus(Status.FAILURE);
        message.setErrorCode(7658);
        message.setErrorMessage("failed due to network issue.");
        return message;
    }
}
