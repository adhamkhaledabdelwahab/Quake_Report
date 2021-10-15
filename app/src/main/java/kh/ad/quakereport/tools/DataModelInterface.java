package kh.ad.quakereport.tools;

import kh.ad.quakereport.model.JsonModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataModelInterface {

    @GET("query")
    Call<JsonModel> getData(@Query("format") String format,
                            @Query("orderby") String orderby,
                            @Query("minmag") float minmag);

    @GET("query")
    Call<JsonModel> getData(@Query("format") String format,
                            @Query("orderby") String orderby,
                            @Query("minmag") float minmag,
                            @Query("starttime") String starttime,
                            @Query("endtime") String endtime);
}
