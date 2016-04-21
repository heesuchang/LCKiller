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
		 
        // 데이터 경계선
        String delimiter = "\r\n--" + boundary + "\r\n";
 
        StringBuffer postDataBuilder = new StringBuffer();
 
        try{
        // 추가하고 싶은 Key & Value 추가
        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
        postDataBuilder.append(delimiter);
 
        // 파일 첨부
        postDataBuilder.append(setFile("uploadFile", dirname +"/"+ filename));
        postDataBuilder.append("\r\n");
 
        // 커넥션 생성 및 설정
        HttpURLConnection conn = (HttpURLConnection) new URL(addr)
                .openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="
                + boundary);
 
        // 전송 작업 시작
        FileInputStream in = new FileInputStream(dirname + "/" + filename);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                conn.getOutputStream()));
        // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
        out.writeUTF(postDataBuilder.toString());
 
        // 파일 복사 작업 시작
        int maxBufferSize = 1024;
        int bufferSize = Math.min(in.available(), maxBufferSize);
        byte[] buffer = new byte[bufferSize];
 
        // 버퍼 크기만큼 파일로부터 바이트 데이터를 읽는다.
        int byteRead = in.read(buffer, 0, bufferSize);
 
        // 전송
        while (byteRead > 0) {
            out.write(buffer);
            bufferSize = Math.min(in.available(), maxBufferSize);
            byteRead = in.read(buffer, 0, bufferSize);
        }
 
        out.writeBytes(delimiter); // 반드시 작성해야 한다.
        out.flush();
        out.close();
        in.close();
 
        // 결과 반환 (HTTP RES CODE)
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
     * Map 형식으로 Key와 Value를 셋팅한다.
     * @param key : 서버에서 사용할 변수명
     * @param value : 변수명에 해당하는 실제 값
     * @return
     */
    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n"
                + value;
    }
 
    /**
     * 업로드할 파일에 대한 메타 데이터를 설정한다.
     * @param key : 서버에서 사용할 파일 변수명
     * @param fileName : 서버에서 저장될 파일명
     * @return
     */
    public static String setFile(String key, String fileName) {
        return "Content-Disposition: form-data; name=\"" + key
                + "\";filename=\"" + fileName + "\"\r\n";
    }

}