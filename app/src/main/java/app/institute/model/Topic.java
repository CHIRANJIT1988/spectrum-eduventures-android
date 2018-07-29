package app.institute.model;

/**
 * Created by CHIRANJIT on 8/8/2016.
 */
public class Topic
{
    public Unit unit = new Unit();

    private String topic_id;
    public String topic_name;

    public Topic(Unit unit, String topic_id, String topic_name)
    {
        this.unit = unit;
        this.topic_id = topic_id;
        this.topic_name = topic_name;
    }
}