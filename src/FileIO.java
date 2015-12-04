//
//import com.sun.corba.se.impl.logging.ORBUtilSystemException;
//import java.io.File;
//import java.io.FileOutputStream;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.NodeList;
//
//
//public class FileIO {
//    public static String regex = " | ";
//
////    public static SiteClass loadXmlFile(File f) throws Exception{
////        SiteClass sc;
////        String title = "";
////        String url = "";
////
////        //document読み込み
////        Document document = readXML(f);
////        
////        //パース開始
////        
////        
////        
////    } 
//
//        
//    public static void saveXmlFile(SiteClass sc) throws Exception{
//        
//    //ドキュメントを作成
//    System.out.println("save - "+sc.getPageName());
//    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//    DocumentBuilder builder = factory.newDocumentBuilder();
//    Document document = builder.newDocument();
//
//    Element rootElement = document.createElement("properties");
//    document.appendChild(rootElement);
//
//    //設定情報
//    //<editor-fold defaultstate="collapsed" desc="element">
//        Element configElement = document.createElement("config");
//        rootElement.appendChild(configElement);
//
//        Element versionElement = document.createElement("version");
//        versionElement.appendChild(document.createTextNode(SystemVariable.APPLET_VERSION.getString()));
//        configElement.appendChild(versionElement);
//
//        Element titleElement = document.createElement("pageName");
//        titleElement.appendChild(document.createTextNode(sc.getPageName()));
//        configElement.appendChild(titleElement);
//
//        Element urlElement = document.createElement("URL");
//        urlElement.appendChild(document.createTextNode(sc.getPageURL()));
//        configElement.appendChild(urlElement);
//
//        Element titleExcludeElement = document.createElement("replaceStr");
//        titleExcludeElement.appendChild(document.createTextNode(sc.getReplaceString()));
//        configElement.appendChild(titleExcludeElement);
//
//        Element titleExcludeWayElement = document.createElement("replaceWay");
//        titleExcludeWayElement.appendChild(document.createTextNode(sc.getReplaceWay()));
//        configElement.appendChild(titleExcludeWayElement);
//
//        Element analysisNumElement = document.createElement("analysisNum");
//        analysisNumElement.appendChild(document.createTextNode(String.valueOf(sc.getAnalysisNum())));
//        configElement.appendChild(analysisNumElement);
//
//        Element downloadNumElement = document.createElement("downloadNum");
//        downloadNumElement.appendChild(document.createTextNode(String.valueOf(sc.getDownloadNum())));
//        configElement.appendChild(downloadNumElement);
//        
//        Element urlRegexElement = document.createElement("urlRegex");
//        configElement.appendChild(urlRegexElement);
//        
//        String[] urlRegex = sc.getUrlRegex();
//        for (String regex : urlRegex) {
//            Element regexElement = document.createElement("regex");
//            regexElement.appendChild(document.createTextNode(regex));
//            urlRegexElement.appendChild(regexElement);
//        }
//    //</editor-fold>
//
//    //UrlQueue情報
//    //<editor-fold defaultstate="collapsed" desc="element">
//        Element urlQueueElement = document.createElement("urlQueue");
//        rootElement.appendChild(urlQueueElement);
//        
//        UrlClass[] queueList = sc.getUrlQueue();
//        for (UrlClass queueList1 : queueList) {
//            Element urlListItemElement = document.createElement("item");
//            urlListItemElement.setTextContent(queueList1.getDataSplit(regex));
//            urlQueueElement.appendChild(urlListItemElement); 
//        }
//    //</editor-fold>
//
//    //UrlRecord情報
//    //<editor-fold defaultstate="collapsed" desc="element">
//        Element urlRecordElement = document.createElement("urlRecord");
//        rootElement.appendChild(urlRecordElement);
//        
//        UrlClass[] recordList = sc.getRecord();
//        for (UrlClass recordList1 : recordList) {
//            Element urlListItemElement = document.createElement("item");
//            urlListItemElement.setTextContent(recordList1.getDataSplit(regex));
//            urlRecordElement.appendChild(urlListItemElement); 
//        }
//    //</editor-fold>
//
//    //RecordList情報
//    //<editor-fold defaultstate="collapsed" desc="element">
//        Element urlExceptElement = document.createElement("urlExcept");
//        rootElement.appendChild(urlExceptElement);
//
//        UrlClass[] exceptList = sc.getExcept();
//        for (UrlClass exceptList1 : exceptList) {
//            Element urlListItemElement = document.createElement("item");
//            urlListItemElement.setTextContent(exceptList1.getDataSplit(regex));
//            urlExceptElement.appendChild(urlListItemElement); 
//        }
//    //</editor-fold>
//
//    //パス作成
//    String path = SystemVariable.SAVE_FILE_PATH.getString()
//            + File.separator
//            + sc.getPageName()
//            + ".xml";
//
//    //フォルダ作成
//    File file = new File(path);
//        if (!file.getParentFile().exists()){
//            file.getParentFile().mkdirs();
//        }
//
//    //XML作成
//    writeXML(file, document);
//
//
//    }
//
//    private static void writeXML(File f, Document document) throws Exception {
//       FileOutputStream fos = null;
//        
//        try {
//            fos = new FileOutputStream(f); 
//            StreamResult result = new StreamResult(fos); 
//
//            // Transformerファクトリを生成
//            TransformerFactory transFactory = TransformerFactory.newInstance();
//            // Transformerを取得
//            Transformer transformer = transFactory.newTransformer(); 
//
//            // エンコード：UTF-8、インデントありを指定
//            transformer.setOutputProperty("encoding", SystemVariable.XML_CHARCODE.getString());
//            transformer.setOutputProperty("indent", "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
//
//            // transformerに渡すソースを生成
//            DOMSource source = new DOMSource(document);
//            
//            // 出力実行
//            transformer.transform(source, result);
//            System.out.println("xml - "+f.getPath());
//        } finally {
//            if (fos != null)    fos.close();
//        }
//    }
//
//    private static Document readXML(File f) throws Exception{
//        //document作成
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document doc = db.parse(f);
//        
//        return doc;        
//    }
//}
