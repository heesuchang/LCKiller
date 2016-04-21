package com.ringdroid;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpManager {
	
	String send(String addr, String dirname, String filename) {
		String result = null;
		String boundary = "^******^";
		 
        // ������ ��輱
        String delimiter = "\r\n--" + boundary + "\r\n";
 
        StringBuffer postDataBuilder = new StringBuffer();
 
        try{
        // �߰��ϰ� ���� Key & Value �߰�
        // key & value�� �߰��� �� �� ��輱�� ��������� �����͸� ������ �� �ִ�.
        postDataBuilder.append(delimiter);
 
        // ���� ÷��
        postDataBuilder.append(setFile("uploadFile", dirname +"/"+ filename));
        postDataBuilder.append("\r\n");
 
        // Ŀ�ؼ� ���� �� ����
        HttpURLConnection conn = (HttpURLConnection) new URL(addr)
                .openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="
                + boundary);
 
        // ���� �۾� ����
        FileInputStream in = new FileInputStream(dirname + "/" + filename);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                conn.getOutputStream()));
        // ������ �ۼ��� ��Ÿ�����͸� ���� �����Ѵ�. (�ѱ��� ���ԵǾ� �����Ƿ� UTF-8 �޼ҵ� ���)
        out.writeUTF(postDataBuilder.toString());
 
        // ���� ���� �۾� ����
        int maxBufferSize = 1024;
        int bufferSize = Math.min(in.available(), maxBufferSize);
        byte[] buffer = new byte[bufferSize];
 
        // ���� ũ�⸸ŭ ���Ϸκ��� ����Ʈ �����͸� �д´�.
        int byteRead = in.read(buffer, 0, bufferSize);
 
        // ����
        while (byteRead > 0) {
            out.write(buffer);
            bufferSize = Math.min(in.available(), maxBufferSize);
            byteRead = in.read(buffer, 0, bufferSize);
        }
 
        out.writeBytes(delimiter); // �ݵ�� �ۼ��ؾ� �Ѵ�.
        out.flush();
        out.close();
        in.close();
 
        // ��� ��ȯ (HTTP RES CODE)
        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            str.append(line);
        }
        result = str.toString();
        conn.disconnect();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	return result;
        }
		
}
    
 
    /**
     * Map �������� Key�� Value�� �����Ѵ�.
     * @param key : �������� ����� ������
     * @param value : ������ �ش��ϴ� ���� ��
     * @return
     */
    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n"
                + value;
    }
 
    /**
     * ���ε��� ���Ͽ� ���� ��Ÿ �����͸� �����Ѵ�.
     * @param key : �������� ����� ���� ������
     * @param fileName : �������� ����� ���ϸ�
     * @return
     */
    public static String setFile(String key, String fileName) {
        return "Content-Disposition: form-data; name=\"" + key
                + "\";filename=\"" + fileName + "\"\r\n";
    }

}