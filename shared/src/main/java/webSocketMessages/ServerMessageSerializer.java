package webSocketMessages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.*;



import java.lang.reflect.Type;

public class ServerMessageSerializer implements JsonSerializer<ServerMessage> {

    @Override
    public JsonElement serialize(ServerMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverMessageType", src.getServerMessageType().toString());


        if (src instanceof LoadGame) {
            jsonObject.add("game", context.serialize(((LoadGame) src).getGame()));

        } else if (src instanceof Error) {
            jsonObject.addProperty("errorMessage", ((Error) src).getErrorDescription());

        } else if (src instanceof Notification) {
            jsonObject.addProperty("message", ((Notification) src).getNotification());

        }
        return jsonObject;
    }
}
