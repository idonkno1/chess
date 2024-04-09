package webSocketMessages;

import com.google.gson.*;
import webSocketMessages.serverMessages.*;

import java.lang.reflect.Type;

public class ServerMessageSerializer implements JsonSerializer<ServerMessage> {

    @Override
    public JsonElement serialize(ServerMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverMessageType", src.getServerMessageType().toString());


        if (src instanceof LoadGameMessage) {

            jsonObject.add("game", context.serialize(((LoadGameMessage) src).getGame()));
        } else if (src instanceof ErrorMessage) {

            jsonObject.addProperty("errorDescription", ((ErrorMessage) src).getErrorDescription());
        } else if (src instanceof NotificationMessage) {

            jsonObject.addProperty("notification", ((NotificationMessage) src).getNotification());
        }

        return jsonObject;
    }
}
