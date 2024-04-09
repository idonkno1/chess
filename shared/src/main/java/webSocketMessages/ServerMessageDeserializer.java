package webSocketMessages;


import com.google.gson.*;
import webSocketMessages.serverMessages.*;

import java.lang.reflect.Type;

public class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {

    @Override
    public ServerMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("serverMessageType").getAsString();

        switch (ServerMessage.ServerMessageType.valueOf(type)) {
            case LOAD_GAME:
                return context.deserialize(json, LoadGameMessage.class);
            case ERROR:
                return context.deserialize(json, ErrorMessage.class);
            case NOTIFICATION:
                return context.deserialize(json, NotificationMessage.class);
            case HIGHLIGHT:
                return context.deserialize(json, HighlightMessage.class);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }
}
