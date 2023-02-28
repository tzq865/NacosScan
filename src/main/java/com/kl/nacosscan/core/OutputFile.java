package com.kl.nacosscan.core;

import cn.hutool.core.io.file.FileAppender;

import java.io.*;
import java.util.Date;

public class OutputFile {

    private static final String INFO_FILE = "info.txt";
    private static final String IP_FILE = "ip.txt";
    private static final String EMAIL_FILE = "email.txt";
    private static final String PHONE_FILE = "phone.txt";
    private static final String PASS_FILE = "pass.txt";
    private static final String URL_FILE = "url.txt";

    private static String filePath;

    public OutputFile(File path) throws FileNotFoundException{
        System.out.println(path.getAbsolutePath());
        if(path.exists()){
            String dirstr = new Date().getTime() + "";
            File dirFile = new File(path.getAbsolutePath()+"/"+dirstr);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            filePath = dirFile.getAbsolutePath();
        }else{
            throw new FileNotFoundException("目录不存在");
        }
    }

    private static OutputFile single=null;
    //静态工厂方法
    public static OutputFile getInstance() throws FileNotFoundException {
        File file = new File("./result");
        if(!file.exists()) file.mkdir();
        if (single == null) {
            single = new OutputFile(file);
        }
        return single;
    }

    //静态工厂方法
    public static OutputFile getInstance(String path) throws FileNotFoundException {
        if (single == null) {
            single = new OutputFile(new File(path));
        }
        return single;
    }


    public void outputINFO(String data){
        File file = new File(filePath + "\\" + INFO_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public void outputIP(String data){
        File file = new File(filePath + "\\" + IP_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public void outputEmail(String data){
        File file = new File(filePath + "\\" + EMAIL_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public void outputPhone(String data){
        File file = new File(filePath + "\\" + PHONE_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public void outputPass(String data){
        File file = new File(filePath + "\\" + PASS_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public void outputUrl(String data){
        File file = new File(filePath + "\\" + URL_FILE);
        try {
            if(!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileAppender fileAppender = new FileAppender(file,10,true);
        fileAppender.append(data);
        fileAppender.flush();
    }

    public String getOutputPath(){
        return filePath;
    }

    public static void main(String[] args) {
        try {
            OutputFile output = OutputFile.getInstance();
            output.outputINFO("1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
