package com.davidecolombo.noip.noip;

import com.davidecolombo.noip.retrofit.BasicAuthInterceptor;
import com.davidecolombo.noip.retrofit.UserAgentInterceptor;

import lombok.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface INoipApi {

	/*
	 * Updates are performed by making an HTTP request to the following URL.
	 */
	static final String BASE_URL = "https://dynupdate.no-ip.com/";

	@GET("nic/update")
	public Call<String> update(@Query("hostname") String hostname, @Query("myip") String myip);

	public static INoipApi build(@NonNull String username, @NonNull String password, @NonNull String userAgent) {
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(new BasicAuthInterceptor(username, password))
				.addInterceptor(new UserAgentInterceptor(userAgent))
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.client(client)
				.baseUrl(INoipApi.BASE_URL)
				.addConverterFactory(ScalarsConverterFactory.create())
				.build();
		return retrofit.create(INoipApi.class);
	}
}
