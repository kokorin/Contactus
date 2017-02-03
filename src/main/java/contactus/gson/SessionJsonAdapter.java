package contactus.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import contactus.core.Session;

import java.io.IOException;
import java.time.Instant;

public class SessionJsonAdapter extends TypeAdapter<Session> {
    @Override
    public void write(JsonWriter out, Session session) throws IOException {
        out.beginObject()
            .name("userId").value(session.getUserId())
            .name("token").value(session.getAccessToken())
            .name("expiration").value(session.getExpiration().toEpochMilli())
        .endObject();
    }

    @Override
    public Session read(JsonReader in) throws IOException {
        in.beginObject();
        Integer userId = null;
        String token = null;
        Instant expiration = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "userId":
                    userId = in.nextInt();
                    break;
                case "token":
                    token = in.nextString();
                    break;
                case "expiration":
                    expiration = Instant.ofEpochMilli(in.nextLong());
                    break;
            }
        }
        in.endObject();

        return new Session(userId, token, expiration);
    }
}
