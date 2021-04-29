/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.utilities.Connect;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 *
 * @author alex
 */
public class Demo {
    public static void main(String[] args) {
        
//        OldBook[] books = { new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686")
//                ,new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz")
//                ,new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880")
//                ,new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123204587")
//                ,new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823")
//        };  
          Document doc = Connect.connectToURL("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=122260932");
          for (Element e : doc.getAllElements()) {
              for (Node node : e.childNodes()) {
                  if(node instanceof Comment) {
                      Comment comment = (Comment) node;
                      System.out.println(comment.getData());
                  }
              }
            
                  
          }
          
//        ExcelWriter writer = new ExcelWriter(
//                Connect.newWorkbookFromTemplate());
//        JFrame frame = new JFrame("JWPdemo");
//        JPanel panel = new JPanel();
//        JTextArea textArea = new JTextArea(25, 30);
//        File saveFile;
//        
//        JButton addButton = new JButton(Handlers.anonymousEventClass("add", event ->{
//            try {
//                writer.writeBooks(OldBook.retrieveBookArray(textArea));
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                System.out.println(e.getCause());
//                e.printStackTrace();
//                System.out.println("Something wrong with input.");
//            }
//            
//            System.out.println("added");
//            
//        }));
//        JButton saveButton = new JButton(Handlers.anonymousEventClass("save", event ->{
//            
//            writer.saveFile(FileUtility.saveLocation(panel));
//            System.out.println("saved.");
//            
//        }));
//        
//        panel.add(addButton);
//        panel.add(saveButton);
//        panel.add(textArea);
//        frame.add(panel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        java.awt.EventQueue.invokeLater(()-> {
//            frame.pack();
//            frame.setVisible(true);
//            
//        });
        
        //FileUtility.openFile(panel);
        
//        
//        OldBook book = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939");
//        System.out.println(book.getAuthor());
//        System.out.println(book.getAuthor2());
//        System.out.println(book.getPublisher());
//        System.out.println(book.titleExists());
//        
        
//        try {
//            XSSFWorkbook wkb = Connect.newWorkbookFromTemplate();
//            XSSFSheet sheet = wkb.getSheetAt(0);
//            for (int i = 0; i < 15; i++) {
//                System.out.print(i);
//                Cell cell = sheet.getRow(i).getCell(1);
//                if ( cell == null) System.out.print("null");
//                else {
//                    if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//                        cell.setCellValue("blank");
//                    }
//                }
//                System.out.println();
//            }
//            
//            //XSSFRow row = sheet.createRow(4);
//           // row.createCell(0).setCellValue("hey, i did it");
//        
//            FileOutputStream fos = new FileOutputStream(new File("test.xlsx"));
//                wkb.write(fos);
//                fos.close();
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //OldBook book = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=120057939&start=wz");
        //OldBook book = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123015880");
        //OldBook book = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=119979686");
        //OldBook book = new OldBook("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=121719823");
        //book.retrieveAuthorInfo();
        
        
        
        
        //http://image.aladin.co.kr/product/12171/98/letslook/K132531341_f.jpg
        //http://image.aladin.co.kr/product/12005/79/letslook/8901219948_f.jpg
        //http://image.aladin.co.kr/product/12171/98/cover/k132531341_1.jpg
//        String url = "http://image.aladin.co.kr/product/12005/79/cover/8901219948_1.jpg";
//        StringBuilder sb = new StringBuilder(url);
//        sb.replace(sb.indexOf("cover"), sb.indexOf("cover")+5, "letslook");
//        sb.setCharAt(sb.lastIndexOf("1"), 'f');
//        System.out.println(sb.toString());
        
        
        
        
        
////
//        for (OldBook book: books) {
//            System.out.println(book.getISBN());
//            System.out.println(book.getDescription());
//            System.out.println();
//            System.out.println();
//            System.out.println();
//            System.out.println("NEWLINE");
//        }
//        System.out.println(book.doc.getElementsByClass("p_goodstd03").text());
//        StringTokenizer st = new StringTokenizer(book.doc.getElementsByClass("p_goodstd03").text(), " |");
//        while(st.hasMoreTokens()) System.out.println(st.nextToken());

//        Elements meta = (book.doc.getElementsByTag("meta"));
//        System.out.println(book.doc.select("[property = 'og.title']"));
//        Pattern pattern = Pattern.compile("(\\d+)");
//        Matcher m = pattern.matcher("1233232쪽");
//        if (m.find()) {
//            System.out.println("yes");
//            System.out.println(m.group());
//        }
        
        
//        System.out.println(book.doc.getElementsByAttributeValueMatching(
//                "style", "height:25px; padding-right:10px;").text());        
        
    }
    
}
