package com.example.myproject.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myproject.DBEntities.DBCustomer;
import com.example.myproject.DBEntities.QDBCustomer;
import com.example.myproject.service.CustomerService;
import com.example.myproject.utils.ResourceNotFound;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mysema.query.jpa.impl.JPAQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@ComponentScan("com.example.myproject")
public class CustomerRest {

	/*static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	    
	   
	}*/

	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntityManager entitymanager;

	@Autowired
	CustomerService customerService;

	LoadingCache<Long, DBCustomer> customerCache = CacheBuilder.newBuilder().maximumSize(100) // maximum
																								// 100
																								// records
																								// can
																								// be
																								// cached
			.expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after
														// 30 minutes of access
			.build(new CacheLoader<Long, DBCustomer>() {
				@Override
				public DBCustomer load(Long empId) throws Exception {
					// make the expensive call
					return getFromDatabase(empId);
				}
			});

	private DBCustomer getFromDatabase(Long custId) {
		logger.debug("From Database!");
		QDBCustomer qCustomer = QDBCustomer.dBCustomer;
		JPAQuery query = new JPAQuery(entitymanager);
		DBCustomer dbCust = query.from(qCustomer).where(qCustomer.id.eq(custId)).uniqueResult(qCustomer);
		return dbCust;
	}

	@RequestMapping("/customer/{id}")
	DBCustomer getCustomerById(@PathVariable("id") Long id) {
		DBCustomer dbCust;
		try {
			dbCust = customerCache.get(id);
		} catch (Exception e) {
			throw new ResourceNotFound();
		}

		return dbCust;

	}
	
	@RequestMapping("/customers/")
	String getAllCustomersEndpoint() {
		QDBCustomer qCustomer = QDBCustomer.dBCustomer;
		JPAQuery query = new JPAQuery(entitymanager);
		List<DBCustomer> dbCust = query.from(qCustomer).list(qCustomer);
		
		return dbCust.toString();
	}

	@RequestMapping("/customer/")
	String getAllCustomers() {
		/*QDBCustomer qCustomer = QDBCustomer.dBCustomer;
		JPAQuery query = new JPAQuery(entitymanager);
		List<DBCustomer> dbCust = query.from(qCustomer).list(qCustomer);*/
		try {
			bypassSelfSignedCertificates();
			return "yup";//getHTML("https://localhost:8443/customer/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "nope";
	}

	@RequestMapping("/customer/{firstName}/{lastName}")
	DBCustomer saveCustomer(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		return customerService.save(new DBCustomer(firstName, lastName));
	}
	
	 public String getHTML(String urlToRead) throws Exception {
	      StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }
	
	 private TrustManager[ ] get_trust_mgr() {
	     TrustManager[ ] certs = new TrustManager[ ] {
	        new X509TrustManager() {
	           public X509Certificate[ ] getAcceptedIssuers() { return null; }
	           public void checkClientTrusted(X509Certificate[ ] certs, String t) { }
	           public void checkServerTrusted(X509Certificate[ ] certs, String t) { }
	         }
	      };
	      return certs;
	  }

	  private void bypassSelfSignedCertificates(){
	     String https_url = "https://localhost:8443/customers/";
	     URL url;
	     try {

		    // Create a context that doesn't check certificates.
	            SSLContext ssl_ctx = SSLContext.getInstance("TLS");
	            TrustManager[ ] trust_mgr = get_trust_mgr();
	            ssl_ctx.init(null,                // key manager
	                         trust_mgr,           // trust manager
	                         new SecureRandom()); // random number generator
	            HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

		    url = new URL(https_url);
		    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

		    // Guard against "bad hostname" errors during handshake.
	            con.setHostnameVerifier(new HostnameVerifier() {
	                public boolean verify(String host, SSLSession sess) {
	                    if (host.equals("localhost")) return true;
	                    else return false;
	                }
	            });

		    //dumpl all cert info
		    print_https_cert(con);

		    //dump all the content
		    print_content(con);

		 } catch (MalformedURLException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		 }catch (KeyManagementException e) {
			e.printStackTrace();
	      }
	   }

	  private void print_https_cert(HttpsURLConnection con){
	     if(con!=null){

	     try {

		System.out.println("Response Code : " + con.getResponseCode());
		System.out.println("Cipher Suite : " + con.getCipherSuite());
		System.out.println("\n");

		Certificate[] certs = con.getServerCertificates();
		for(Certificate cert : certs){
		  System.out.println("Cert Type : " + cert.getType());
		  System.out.println("Cert Hash Code : " + cert.hashCode());
		  System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
		  System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
		  System.out.println("\n");
		}


	     } catch (SSLPeerUnverifiedException e) {
		  e.printStackTrace();
	     } catch (IOException e){
		  e.printStackTrace();
	     }
	   }
	  }

	  private void print_content(HttpsURLConnection con){
	    if(con!=null){

	    try {

		System.out.println("****** Content of the URL ********");

		BufferedReader br =
			new BufferedReader(
				new InputStreamReader(con.getInputStream()));

		String input;

		while ((input = br.readLine()) != null){
		   System.out.println(input);
		}
		br.close();

	     } catch (IOException e) {
		e.printStackTrace();
	     }
	   }
	  }
	 
	 
	 
}
