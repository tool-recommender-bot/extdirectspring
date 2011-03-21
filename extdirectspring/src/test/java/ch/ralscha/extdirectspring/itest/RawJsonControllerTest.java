/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class RawJsonControllerTest extends JettyTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testRawResponse() throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringEntity postEntity = new StringEntity(
				"{\"action\":\"rawJsonController\",\"method\":\"listUsers\",\"data\":[],\"type\":\"rpc\",\"tid\":1}",
				"UTF-8");
		post.setEntity(postEntity);

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertNotNull(entity);
		String responseString = EntityUtils.toString(entity);

		assertNotNull(responseString);
		assertTrue(responseString.startsWith("[") && responseString.endsWith("]"));
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> rootAsMap = mapper.readValue(responseString.substring(1, responseString.length() - 1),
						Map.class);
		assertEquals(5, rootAsMap.size());

		assertEquals("listUsers", rootAsMap.get("method"));
		assertEquals("rpc", rootAsMap.get("type"));
		assertEquals("rawJsonController", rootAsMap.get("action"));
		assertEquals(1, rootAsMap.get("tid"));
		
		Map<String,Object> result = (Map<String,Object>)rootAsMap.get("result");
		assertEquals(2, result.size());
		assertTrue((Boolean)result.get("success"));

		List<Map<String,Object>> records = (List<Map<String,Object>>)result.get("records");
		assertEquals(2, records.size());
		
		assertEquals("4cf8e5b8924e23349fb99454", ((Map<String,Object>)records.get(0).get("_id")).get("$oid"));
		assertEquals("4cf8e5b8924e2334a0b99454", ((Map<String,Object>)records.get(1).get("_id")).get("$oid"));		
				
	}

}
