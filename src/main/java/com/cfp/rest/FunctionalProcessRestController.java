package com.cfp.rest;

import com.fp.model.Process;
import com.mongodb.Mongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class FunctionalProcessRestController {


    @RequestMapping("/{systemContextID}/FunctionalProcess")
    public List getFunctionalProcessList(@PathVariable String systemContextID) {

        //execute a query for all functional processes for a particular systemContext

        Process one = new Process();
        one.setName("A name");
        one.setDescription("A description");

        Process two = new Process();
        two.setName("B Name");
        two.setDescription("B Description");

        List returnList = new ArrayList();
        returnList.add(one);
        returnList.add(two);

        return returnList;

    }

    @RequestMapping(value = "/{systemContextID}/FunctionalProcess/{fpId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String getFunctionalProcess(@PathVariable String systemContextID, @PathVariable String fpId) {


        MongoTemplate template = getMongoTemplate();
        String json = "{\"name\":\"mkyong\", \"age\":21} ";
        String collectionName = "myCollection";
        template.save(json, collectionName);


        BasicQuery query = new BasicQuery("{ age : { $lt : 31 }}", "{name:1,_id:0}}");
        //      The include and exclude methods promise to modify the projection, but I can't get them to work.
        //      BasicQuery query = new BasicQuery("{ age : { $lt : 31 }}");
        //      query.fields().include("name").exclude("_id");
        List<String> result = template.find(query, String.class, collectionName);


        // I have to manually create a JSON array, as Spring doesn't do a good job
        // with the Json Message Converters. Perhaps I should create a List Converter.
        // see the MvcConfig class to see how Marshallers are created.
        StringBuilder unescapedJson = new StringBuilder();
        unescapedJson.append("[");

        for (Iterator<String> iterator = result.iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            unescapedJson.append(next);
            if (iterator.hasNext()) {
                unescapedJson.append(",");
            }
        }

        unescapedJson.append("]");
        return unescapedJson.toString();
    }

    private MongoTemplate getMongoTemplate() {
        Mongo mongo = null;
        try {
            mongo = new Mongo("apdb-atpmdev", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        SimpleMongoDbFactory factory = new SimpleMongoDbFactory(mongo, "cfp");
        return new MongoTemplate(factory);
    }


    @RequestMapping(value = "/SystemContext/{systemContextID}", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateSystemContext(@PathVariable String systemContextID) {

        MongoTemplate mongo = getMongoTemplate();

        return null;

    }
}
