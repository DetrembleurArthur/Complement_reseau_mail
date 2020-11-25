package org.bourgedetrembleur;

import java.util.ArrayList;

public class Tracker
{
    private String date;
    private String from;
    private String by;
    private String via;
    private String id;
    private String _for;
    private String with;

    public Tracker(String receiveExpression)
    {
        String[] splitted = receiveExpression.split(";");
        if(splitted.length == 2)
            date = splitted[1].replace("\n", "");
        else
            date = "/";
        String content = splitted[0];
        System.err.println(receiveExpression);

        from = extract(content, "from ");
        by = extract(content, "by ");
        via = extract(content, "via ");
        id = extract(content, "id ");
        _for = extract(content, "for ");
        with = extract(content, "with ");

    }

    private String extract(String expr, String keyword)
    {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("from ");
        keywords.add("by ");
        keywords.add("via ");
        keywords.add("id ");
        keywords.add("for ");
        keywords.add("with ");
        keywords.remove(keyword);
        if(expr.contains(keyword))
        {
            var tokens = expr.split(keyword);
            String token = tokens[tokens.length-1];
            int i = token.length();
            for(var tok : keywords)
            {
                int j = token.indexOf(tok);
                if(j != -1)
                {
                    if(j < i)
                        i = j;
                }
            }
            return token.substring(0, i);
        }

        return "/";
    }

    public String getDate()
    {
        return date;
    }

    public String getFrom()
    {
        return from;
    }

    public String getBy()
    {
        return by;
    }

    public String getVia()
    {
        return via;
    }

    public String getId()
    {
        return id;
    }

    public String getFor()
    {
        return _for;
    }

    public String getWith()
    {
        return with;
    }
}
