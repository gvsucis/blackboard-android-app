package edu.gvsu.bbmobile.MyBB.helpers.bbannouncements;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 10/18/13.
 */
public class XMLAnnouncementParser {

    private BbAnnouncement announcement;
    private String text;
    private boolean isCdata = false;
    private boolean inEmImages = false;
    private boolean inEmImage = false;

    public List<BbAnnouncement> parse(String in) {

        List<BbAnnouncement> theRet = new ArrayList<BbAnnouncement>();

        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(new StringReader(in));
            Integer eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {

                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("announcement")) {
                            // create a new instance of employee
                            announcement = new BbAnnouncement();
                        } else if(tagname.equalsIgnoreCase("desc")){
                            isCdata = true;
                            eventType = parser.nextToken();
                        } else if (tagname.equalsIgnoreCase("em_images")){
                            inEmImages = true;
                        } else if (tagname.equalsIgnoreCase("image") && inEmImages){
                            inEmImage = true;
                        }

                    break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.CDSECT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("announcement")) {
                            // add employee object to list
                            theRet.add(announcement);
                        } else if (tagname.equalsIgnoreCase("id")) {
                            announcement.id = text;
                        } else if (tagname.equalsIgnoreCase("label")) {
                            announcement.label = text;
                        } else if (tagname.equalsIgnoreCase("pos")) {
                            announcement.pos = text;
                        } else if (tagname.equalsIgnoreCase("crs_id")) {
                            announcement.crsId = text;
                        } else if (tagname.equalsIgnoreCase("crs_name")) {
                            announcement.crsName = text;
                        } else if (tagname.equalsIgnoreCase("desc")){
                            announcement.description = MyGlobal.replaceVTBEURLStub(text);
                        } else if (tagname.equalsIgnoreCase("link") && inEmImages && inEmImage){
                            announcement.emImageLinks.add(text);
                        } else if (tagname.equalsIgnoreCase("image") && inEmImages){
                            inEmImage = false;
                        } else if (tagname.equalsIgnoreCase("em_images")){
                            inEmImages = false;
                        }


                        break;

                    default:
                        break;
                }
                if(!isCdata){
                    eventType = parser.next();
                }
                isCdata = false;
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return theRet;
    }
}
