package org.smallcode.gui;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.internal.InternalClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smallcode.net.ESQueries;
import org.smallcode.net.JSONClient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.smallcode.gui.StringConstants.*;

/**
 * Created with IntelliJ IDEA.
 * User: hikmat
 * Date: 9/10/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESWindow extends JFrame {

    JSONObject jsonObject;

    JTextField addressBar;
    JTextField indexBar;
    JButton goButton;
    Container container;

    JPanel panelTop;

    JPanel panelCentre;
    JPanel panelBottom;

    JLabel clusterName;
    JLabel numberOfNodes;
    JLabel activePrimaryShards;
    JLabel activeShards;
    JLabel status;

    JLabel clusterName1;
    JLabel numberOfNodes1;
    JLabel activePrimaryShards1;
    JLabel activeShards1;
    JLabel status1;

    JTextField recordType;
    JTextField field;
    JButton export;

    public ESWindow(){
        super("ES Data View");
        setSize(600,400);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        populateWindow();

        setVisible(true);
        setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void initializeComponents(){

        addressBar=new JTextField("http://localhost:9200");
        indexBar=new JTextField("1003");
        goButton=new JButton("Hit");
        goButton.setActionCommand("Hit");

        recordType=new JTextField("RecordType...");
        field=new JTextField("Query string..");
        export=new JButton("Export");
        export.setActionCommand("Export");


        panelTop=new JPanel(new GridLayout(2,3));
        panelTop.setBackground(Color.WHITE);

        panelCentre =new JPanel(new GridLayout(1,1));
        panelCentre.setBackground(Color.WHITE);

        panelBottom =new JPanel(new GridLayout(5,2));
        panelBottom.setBackground(Color.WHITE);

        clusterName=new JLabel("Cluster Name:");
        numberOfNodes=new JLabel("Number Of Nodes:");
        activePrimaryShards =new JLabel("Active Primary Shards");
        activeShards=new JLabel("Active Shards:");
        status=new JLabel("Status:");

        clusterName1=new JLabel("Unknown");
        numberOfNodes1=new JLabel("Unknown");
        activePrimaryShards1 =new JLabel("Unknown");
        activeShards1=new JLabel("Unknown");
        status1=new JLabel("Unknown");


        panelBottom.add(clusterName);
        panelBottom.add(clusterName1);

        panelBottom.add(numberOfNodes);
        panelBottom.add(numberOfNodes1);

        panelBottom.add(activePrimaryShards);
        panelBottom.add(activePrimaryShards1);

        panelBottom.add(activeShards);
        panelBottom.add(activeShards1);

        panelBottom.add(status);
        panelBottom.add(status1);

        container=getContentPane();
        setLayout(new GridLayout(4,1));

        panelTop.add(addressBar);
        panelTop.add(indexBar);
        panelTop.add(goButton);
        panelTop.add(recordType);
        panelTop.add(field);
        panelTop.add(export);

        container.add(panelTop);
        container.add(panelCentre);
        container.add(panelBottom);

    }

    public void populateWindow(){
       initializeComponents();
       export.addActionListener(new ESEventHandler());
       goButton.addActionListener(new ESEventHandler());
    }

    class  ESEventHandler implements ActionListener{
         @Override
         public void actionPerformed(ActionEvent e) {
             String index=indexBar.getText();
             String type=  recordType.getText();
             String fieldName=field.getText();

             if(e.getActionCommand().equalsIgnoreCase("Hit")){
                   try {
                           jsonObject = JSONClient.fetch(addressBar.getText() + ESQueries.CLUSTER_HEALTH);
                           clusterName1.setText(jsonObject.getString("cluster_name"));
                           numberOfNodes1.setText(jsonObject.getString("number_of_nodes"));
                           activePrimaryShards1.setText(jsonObject.getString("active_primary_shards"));
                           activeShards1.setText(jsonObject.getString("active_shards"));
                           status1.setText(jsonObject.getString("status"));
                           populateReport(getCountReport(index), index);
                   }catch (JSONException je ){showError(je);je.printStackTrace();}
                    catch(UnknownHostException un){showError(un);un.printStackTrace();}
                    catch (ConnectException ce){showError(ce);ce.printStackTrace();}
                    catch (IOException io){showError(io);io.printStackTrace();}
              }else if(e.getActionCommand().equalsIgnoreCase("Export")){
                 try {
                     InternalClient client=mock(InternalClient.class);
                     SearchRequestBuilder builder=new SearchRequestBuilder(client);
                     builder.setQuery(QueryBuilders.matchAllQuery());

                     String query=null;
                     String a=field.getText();
                     if(a.contains("q=")){
                         String[] ar=a.split("=");
                         query = ar[1];
                     }else {
                         builder.setSource(field.getText());
                         query = builder.toString() ;

                     }


                     String pattern = "\\ |\\n";
                     query=query.replaceAll(pattern,"");

                     String restUrl=addressBar.getText() +"/"+index+"/"+type+"/_search?size=1000&source="+query;

                     System.out.println("REST URL::"+query);

                     jsonObject =JSONClient.fetch(restUrl);

                     jsonObject=(JSONObject) jsonObject.get("hits");

                     JSONArray jsonArray = jsonObject.getJSONArray("hits");

                     List<String> values=new ArrayList<String>();

                     for (int i=0;i< jsonArray.length();i++) {
                         JSONObject jsonObject1= jsonArray.getJSONObject(i);
                         jsonObject1= (JSONObject) jsonObject1.get("_source");
                         Object value=  jsonObject1.get(field.getText());
                         values.add(value.toString());

                     }
                     saveData(values);

                 }catch (JSONException je ){showError(je);je.printStackTrace();}
                  catch(UnknownHostException un){showError(un);un.printStackTrace();}
                  catch (ConnectException ce){showError(ce);ce.printStackTrace();}
                  catch (IOException io){showError(io);io.printStackTrace();}
              }
         }

    }

    public HashMap<String, HashMap<String, String>> getCountReport(String index){
        HashMap<String, HashMap<String, String>> reportMap = new HashMap<String, HashMap<String, String>>();
        String recordType = null;
        String recordCount = null;
        HashMap<String, String> countMap = new HashMap<String, String>();
        HashMap<String, String> scrubMap = new HashMap<String, String>();
        try{
            for (String key : recordTypes) {
                recordType = key;

                recordCount = JSONClient.fetch(addressBar.getText() + "/" + index + "/" + recordType + "/_count").getString("count");

                countMap.put(recordType, recordCount);

            }
            reportMap.put("index", countMap);
            System.out.println(addressBar.toString() + "/" + index + "/ClientConfig/_search");
            jsonObject = (JSONObject) JSONClient.fetch(addressBar.getText() + "/" + index + "/ClientConfig/_search").get("hits");
            JSONArray jsonArray = jsonObject.getJSONArray("hits");
            jsonObject = (JSONObject) jsonArray.get(0);
            jsonObject = (JSONObject) jsonObject.get("_source");
            Iterator iterator = jsonObject.keys();

            while (iterator.hasNext()) {
                String k = iterator.next().toString();
                scrubMap.put(k.toString(), jsonObject.getString(k));
            }
        }catch (JSONException je ){showError(je);je.printStackTrace();}
        catch(UnknownHostException un){showError(un);un.printStackTrace();}
        catch (ConnectException ce){showError(ce);ce.printStackTrace();}
        catch (IOException io){showError(io);io.printStackTrace();}

        reportMap.put("scrub", scrubMap);

        return reportMap;
    }

    public void populateReport(HashMap<String, HashMap<String, String>> reportMap, String index){

        JTable table = new JTable();
        DefaultTableModel tm = new DefaultTableModel(tableHeaders,0);

        formatTable(table);

        JScrollPane jScrollPane=new JScrollPane();
        jScrollPane.setSize(new Dimension(800, 200));
        jScrollPane.setViewportView(table);
        table.setFillsViewportHeight(true);

        HashMap<String, String> map = reportMap.get("index");
        HashMap<String, String> map1 = reportMap.get("scrub");


        int i = 1;
        for (String key : recordTypes) {
            java.util.List data = new ArrayList<String>();
            data.add(i);
            data.add(key);
            if (map.containsKey(key)) {
                data.add(map.get(key));
                if (map1.containsKey(key.replace(key.charAt(0), key.toLowerCase().charAt(0)) + "Count"))
                    data.add(map1.get(key.replace(key.charAt(0), key.toLowerCase().charAt(0)) + "Count"));
                else
                    data.add("N/A");
            }
            if(data.get(2).toString().equalsIgnoreCase(data.get(3).toString()))
                data.add("PASS");
            else{
                data.add("FAIL");

            }
            tm.addRow(data.toArray());
            i++;
        }

        String[] a = derivedRecordTypes.split(",");
        for (String key : a) {
            String[] b = key.split(";");

            java.util.List data = new ArrayList<String>();
            data.add(i++);
            data.add(b[0]);
            try{
                String recordCount = JSONClient.fetch(addressBar.getText() + "/" + index + "/" + b[0] + "/_count").getString("count");

                data.add(recordCount);

                if (map1.containsKey(b[1]))
                    data.add(map1.get(b[1]));
                else
                    data.add("N/A");

                if(data.get(2).toString().equalsIgnoreCase(data.get(3).toString()))
                    data.add("PASS");
                else{
                    data.add("FAIL");

                }

                tm.addRow(data.toArray());
            }catch (JSONException je ){showError(je);je.printStackTrace();}
            catch(UnknownHostException un){showError(un);un.printStackTrace();}
            catch (ConnectException ce){showError(ce);ce.printStackTrace();}
            catch (IOException io){showError(io);io.printStackTrace();}
        }
        table.setModel(tm);
        setResultColumnColor(table);

        panelCentre.removeAll();
        panelCentre.add(jScrollPane);
        refreshWindow();
    }

    public void   showError(JSONException e){
        panelCentre.removeAll();
        String error=e.getMessage();
        JLabel label=new JLabel("Parsing Exception::\n"+error);
        label.setBackground(Color.YELLOW);
        panelCentre.add(label);
        refreshWindow();
    } public void   showError(UnknownHostException e){
        panelCentre.removeAll();
        String error=e.getMessage();
        JLabel label=new JLabel("Unknown Host ::\n"+error);
        label.setBackground(Color.YELLOW);
        panelCentre.add(label);
        refreshWindow();
    } public void   showError(ConnectException e){
        panelCentre.removeAll();
        String error=e.getMessage();
        JLabel label=new JLabel("Connection Error::\n"+error);
        label.setBackground(Color.YELLOW);
        panelCentre.add(label);
        refreshWindow();
    } public void   showError(IOException e){
        panelCentre.removeAll();
        String error=e.getMessage();
        JLabel label=new JLabel("File Not Found Error::\n"+error);
        label.setBackground(Color.YELLOW);
        panelCentre.add(label);
        refreshWindow();
    }

    public void  refreshWindow(){
        validate();
        repaint();
    }

    public JTable formatTable(JTable table){
        table.setGridColor(Color.BLACK);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.YELLOW);
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    public JTable setResultColumnColor(JTable table){
        table.setDefaultRenderer(Object.class, new EntryCellRender());
        return table;
    }

    public void saveData(List<String> values) throws IOException{
        File file;
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
        fileChooser.setDialogTitle("Export data to file-SkyView");
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            System.out.println("Saving to " + file.getName());
        } else {
            System.out.println("Open command cancelled by user.");
            return;
        }
        FileOutputStream outputStream=new FileOutputStream(file);
        Iterator<String> iterator= values.iterator();

        while (iterator.hasNext()){
            String out=iterator.next().toString()+"\n";
            outputStream.write(out.getBytes());
        }
        outputStream.flush();
        outputStream.close();

        panelCentre.removeAll();
        JLabel label=new JLabel("Successfully written to file"+" "+file.getAbsolutePath());
        label.setBackground(Color.YELLOW);
        panelCentre.add(label);

        refreshWindow();

    }

    public class EntryCellRender extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cr = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value=="FAIL" && column==4) {
                   cr.setForeground(Color.RED);
            }   else {
                cr.setBackground(Color.BLACK);
                cr.setForeground(Color.YELLOW);
            }
            return cr;
        }
    }

}
