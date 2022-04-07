package com.beezlabs.hiveonserver.bots;

import com.beezlabs.hiveonserver.libs.JavaBotTemplate;
import com.beezlabs.tulip.libs.models.BotExecutionModel;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCampaign extends  JavaBotTemplate{

    public class CompaignDetails{
        public String Title;
        public String template_id;
        public String recipients; // Getting Differnet Types of Recipients id
        public String subject_line;
        public String from_name;
        public String reply_to;
    }

    DefaultHttpClient httpClient = new DefaultHttpClient();
    @Override
    public void botLogic(BotExecutionModel botExecutionModel){
        try{
            CompaignDetails Data = new CompaignDetails();
            Data.Title = botExecutionModel.getProposedBotInputs().get("title").getValue().toString();
            Data.template_id = botExecutionModel.getProposedBotInputs().get("template_id").getValue().toString();
            Data.recipients = botExecutionModel.getProposedBotInputs().get("list_id").getValue().toString();
            Data.subject_line =  botExecutionModel.getProposedBotInputs().get("subject_line").getValue().toString();
            Data.from_name = botExecutionModel.getProposedBotInputs().get("from_name").getValue().toString();
            Data.reply_to = botExecutionModel.getProposedBotInputs().get("reply_to").getValue().toString();
            String Url = botExecutionModel.getProposedBotInputs().get("url").getValue().toString();
            String App_Key = botExecutionModel.getProposedBotInputs().get("app_key").getValue().toString();
            String AudianceId=botExecutionModel.getProposedBotInputs().get("audiance_id").getValue().toString();
            MakeHttpCall(Data,Url,App_Key,AudianceId);


        }catch(Exception e){
            e.printStackTrace();
            failure("Bot Failed");
        }

    }

    public void MakeHttpCall(CompaignDetails compaignData,String Url,String AppKey,String AudianceId) throws Exception{
        try{
            HttpPost httpPost = new HttpPost(Url);
            httpPost.addHeader("content-type","text/plain");
            httpPost.addHeader("Authorization","BASIC "+AppKey);
            String Body = String.format("{" +
                    "\"type\":\"regular\", "+
                    "\"recipients\":{"+
                        "\"list_id\" :\"%s\", "+
                        "},"+
                    "\"settings\" : {"+
                        "\"subject_line\" : \"%s\","+
                        "\"from_name\" : \"%s\","+
                        "\"reply_to\":\"%s\","+
                         "\"title\":\"%s\","+
                         "\"template_id\":\"%s\""+
                        "}" +
                     "\"content_type\":\"template\""+
                    "}");

            StringEntity body= new StringEntity(Body);
            httpPost.setEntity(body);

            HttpResponse httpResponse =  httpClient.execute(httpPost);
            HttpEntity entity =  httpResponse.getEntity();
            System.out.println(entity.getContent());
            Map<String,Object> Res = new HashMap<>();

            if (entity != null) {



                JSONObject obj = new JSONObject(EntityUtils.toString(entity));
                if(httpResponse.getStatusLine().getStatusCode() == 200 ){
                    //response id for createing
                    addVariable("id",obj.get("id"));
                    Res.put("id",obj.get("id").toString());
                    Res.put("create_time",obj.get("create_time").toString());
                    Res.put("recipients",obj.get("recipients").toString());
                    addVariable("data",Res);

                }
                else if(httpResponse.getStatusLine().getStatusCode() == 400 || httpResponse.getStatusLine().getStatusCode() > 400 ){

                    if(obj.has("status")){
                        addVariable("data",Res);
                    }

                }

            }

        }catch(Exception e){
            throw new Exception(e.getMessage());

        }

    }

}