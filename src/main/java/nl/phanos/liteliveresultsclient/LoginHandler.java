package nl.phanos.liteliveresultsclient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author woutermkievit
 */
import nl.phanos.liteliveresultsclient.classes.ParFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nl.phanos.liteliveresultsclient.classes.Wedstrijd;
import nl.phanos.liteliveresultsclient.gui.Main;
import org.apache.http.HttpEntity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.misc.IOUtils;

public class LoginHandler {
    private static LoginHandler dit;
    public static LoginHandler get() throws Exception {
        if(dit==null){
            dit=new LoginHandler();
        }
        return dit;
    }

    private String cookies;
    private HttpClient client = HttpClientBuilder.create().build();
    private final String USER_AGENT = "Mozilla/5.0";
    private String nuid;

    public LoginHandler() throws Exception {
        String url = "https://www.atletiek.nu/login/";

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        String page = this.GetPageContent(url);
        String user=Main.getWindow().UserName;
        String pass=Main.getWindow().Pass;
        List<NameValuePair> postParams
                = this.getFormParams(page, user, pass);
        this.sendPost(url, postParams);
    }
    public void setNuid(String nuid){
        this.nuid = nuid;
    }
    public static boolean isReachable() {
    // Any Open port on other machine
    // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
    try {
        try (Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress("www.atletiek.nu", 80), 2000);
        }
        return true;
    } catch (IOException ex) {
        return false;
    }
    }
    static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}
    public void getZip() throws Exception {
        String url = "https://www.atletiek.nu/feeder.php?page=exportstartlijstentimetronics&event_id=" + nuid + "&forceAlleenGeprinteLijsten=1";
        System.out.println(url);
        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", getCookies());

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("Response Code : " + responseCode);
        //System.out.println(convertStreamToString(response.getEntity().getContent())); 
        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
        String filePath = "tmp.zip";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        int inByte;
        while ((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }
        bis.close();
        bos.close();

        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
        request.releaseConnection();
    }

    public void submitResults(ArrayList<ParFile> files) throws Exception {
        String url = "https://www.atletiek.nu/feeder.php?page=resultateninvoer&do=uploadresultaat&event_id=" + nuid + "";
        System.out.println(url);
        for (int i = 0; i < files.size(); i++) {
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", getCookies());
        MultipartEntity mpEntity = new MultipartEntity();
        System.out.println("files:"+files.size());
        
            File file = files.get(i).resultFile;
            ContentBody cbFile = new FileBody(file, "text/plain");
            mpEntity.addPart("files", cbFile);
            System.out.println("added file to upload");
        
        post.setEntity(mpEntity);

        HttpResponse response = client.execute(post);
        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("Response Code submit: " + responseCode);
        HttpEntity responseEntity = response.getEntity();
        String responseString = "";
        if (responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
        }
        JSONObject obj = null;
        try {
            obj = new JSONObject(responseString);
            System.out.println("succes:"+responseString);
        } catch (Exception e) {
            System.out.println("error:"+responseString);
        }
        if (obj != null) {
            for (Object FileObj : (JSONArray) obj.get("files")) {
                JSONObject JSONFile = (JSONObject) FileObj;
                files.get(i).uploadDate=Calendar.getInstance().getTimeInMillis();
                files.get(i).UploadedAtleten=""+(Integer)JSONFile.get("totaalverwerkt");
                AtletiekNuPanel.panel.addText("Uploaded " + JSONFile.get("name") + " met " + JSONFile.get("totaalverwerkt") + " atleten");
            }
        }
        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString());
        post.releaseConnection();
        }
        //getZip(nuid);
    }

    private String sendPost(String url, List<NameValuePair> postParams)
            throws Exception {

        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", getCookies());
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        post.setEntity(new UrlEncodedFormEntity(postParams));

        HttpResponse response = client.execute(post);

        int responseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        post.releaseConnection();
        //System.out.println(result.toString());
        return result.toString();

    }

    private String GetPageContent(String url) throws Exception {

        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Cookie", getCookies());

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
                : response.getFirstHeader("Set-Cookie").toString());
        request.releaseConnection();
        return result.toString();

    }

    public List<NameValuePair> getFormParams(
            String html, String username, String password)
            throws UnsupportedEncodingException {

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementById("primarycontent");
        Elements inputElements = loginform.getElementsByTag("input");

        List<NameValuePair> paramList = new ArrayList<NameValuePair>();

        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("email")) {
                value = username;
            } else if (key.equals("password")) {
                value = password;
            }

            paramList.add(new BasicNameValuePair(key, value));

        }

        return paramList;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        //if(cookies!=""){
        this.cookies = cookies;
        System.out.println(cookies);
        //}
    }
    public Object[] getEigenWedstrijden() throws Exception{
        ArrayList<Wedstrijd> wedstrijden=new ArrayList<Wedstrijd>();
        String content = GetPageContent("https://www.atletiek.nu/feeder.php?page=search&do=events&search=&predefinedSearchTemplate=3");
        Element overview = Jsoup.parse(content).getElementsByClass("calendarTable").first().getElementsByTag("tbody").first();
        Elements rows = overview.getElementsByTag("tr");
        for (Element row : rows) {
            if(row.hasAttr("onclick")){
                try{
                Wedstrijd w=new Wedstrijd();
                String[] split=row.attr("onclick").split("/");
                w.id=split[split.length-2];
                w.date=row.getElementsByTag("td").first().text();
                w.club=row.getElementsByTag("td").get(3).text().replace(" ","").replace(",","");
                w.name=w.date+" - "+row.getElementsByTag("td").get(1).getElementsByClass("hidden-xs").first().text();
                wedstrijden.add(w);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        }
        return wedstrijden.toArray();
    }
}
