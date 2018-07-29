package app.institute.model;

/**
 * Created by dell on 25-08-2015.
 */
public class Message
{
    public int message_id, read_status;
    public String message, timestamp;

    public Message()
    {

    }

    public Message(String message, String timestamp)
    {
        this.message = message;
        this.timestamp = timestamp;
    }
}