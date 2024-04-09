package websocket;

import com.google.gson.*;
import webSocketMessages.userCommands.*;

import java.lang.reflect.Type;

public class UserGameCommandDeserializer implements JsonDeserializer<UserGameCommand> {

    @Override
    public UserGameCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("currentSquare")) {
            return context.deserialize(json, MakeMove.class);
        } else if (jsonObject.has("playerColor")) {
            return context.deserialize(json, JoinPlayer.class);
        }else {
            String commandTypeStr = jsonObject.get("commandType").getAsString();
            UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(commandTypeStr);

            String authToken = jsonObject.get("authToken").getAsString();
            int gameID = jsonObject.get("gameID").getAsInt();
            return new UserGameCommand(commandType, authToken, gameID);
        }
    }
}
