package com.example.theodorachu.newsapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends ActionBarActivity {

    private TextView rss;

    private class RSSHandler extends DefaultHandler {
        private String[] rssResult;
        private boolean beginArticles, title, firstTitle;

        public RSSHandler(String[] rssResult) {
            this.rssResult = rssResult;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String cdata = new String(ch, start, length);
            if (title && firstTitle && beginArticles) {
                rssResult[0] = rssResult[0] + (cdata.trim()).replaceAll("\\s+", " ");
            }
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs) throws SAXException {
            if (localName.equals("item")) {
                beginArticles = true;
            }
            if (beginArticles && !firstTitle && localName.equals("title")) {
                firstTitle = true;
                title = true;
                rssResult[0] = rssResult[0] + "\n" + localName + ": ";
            } else {
                title = false;
            }
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if(localName.equals("item"))
                firstTitle = false;
        }

    }

    private class DownloadFilesTask extends AsyncTask<URL, Void, Long> {

        private String[] rssResult = new String[1];

        @Override
        protected Long doInBackground(URL... urls) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();

                rssResult[0] = "";
                RSSHandler rssHandler = new RSSHandler(rssResult);
                xmlReader.setContentHandler(rssHandler);
                InputSource inputSource = new InputSource(urls[0].openStream());
                xmlReader.parse(inputSource);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rss.setText(rssResult[0]);
                    }
                });

            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rss = (TextView) findViewById(R.id.rss);
        try {
            URL rssUrl = new URL("http://time.com/politics/feed/");
            URL[] urls = new URL[1];
            urls[0] = rssUrl;

            DownloadFilesTask task = new DownloadFilesTask();
            task.execute(urls);
        } catch (IOException e) {
            e.printStackTrace();
            rss.setText(e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
