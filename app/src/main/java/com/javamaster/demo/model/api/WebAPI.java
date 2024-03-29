package com.javamaster.demo.model.api;


import android.app.Application;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.javamaster.demo.R;
import com.javamaster.demo.model.Model;
import com.javamaster.demo.model.Phone;
import com.javamaster.demo.model.User;
import com.javamaster.demo.model.address.City;
import com.javamaster.demo.model.address.District;
import com.javamaster.demo.model.address.House;
import com.javamaster.demo.model.address.Street;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebAPI implements API {

    private static String BASE_URL;
    private final Application mApplication;
    private final Model mModel;

    private RequestQueue mRequestQueue;

    public WebAPI(Application application, Model model) {
        mApplication = application;
        mRequestQueue = Volley.newRequestQueue(application);
        BASE_URL = mApplication.getString(R.string.base_url);
        mModel = model;
    }

    @Override
    public void login(String login, String password, final APIListener listener) {
        String url = BASE_URL + "auth";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        User user = User.getUser(response);
                        listener.onLogin(user);
                    }
                    catch (JSONException e) {
                        listener.onFailed("JSON exception");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onFailed(getErrorMessage(error));
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener);
            mRequestQueue.add(request);
        }
        catch (JSONException e) {
            listener.onFailed("JSON exception");
        }
    }

    @Override
    public void register(String login, String name, String email, String password, int houseId, final APIListener listener) {
        String url = BASE_URL + "register";
        JSONObject jsonObject = new JSONObject();
        JSONObject userObject = new JSONObject();

        try {
            userObject.put("login", login);
            userObject.put("name", name);
            userObject.put("email", email);
            userObject.put("password", password);

            jsonObject.put("houseId", houseId);
            jsonObject.put("user", userObject);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mes = response.getString("message");
                        listener.onRegistered(mes);
                    }
                    catch (JSONException e) {
                        listener.onFailed("JSON exception");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onFailed(getErrorMessage(error));
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener);
            mRequestQueue.add(request);
        }
        catch (JSONException e) {
            listener.onFailed("JSON exception");
        }
    }

    @Override
    public void recovery(String login, String email, final APIListener listener) {
        String url = BASE_URL + "recovery";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("login", login);
            jsonObject.put("email", email);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mes = response.getString("message");
                        listener.onRecovered(mes);
                    }
                    catch (JSONException e) {
                        listener.onFailed("JSON exception");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onFailed(getErrorMessage(error));
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener);
            mRequestQueue.add(request);
        }
        catch (JSONException e) {
            listener.onFailed("JSON exception");
        }
    }

    @Override
    public void loadPhones(final APIListener listener) {
        String url = BASE_URL + "api/phones";

        Response.Listener<JSONArray> successListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Phone> phones = Phone.getPhones(response);
                    if (listener != null) {
                        listener.onPhonesLoaded(phones);
                    }
                } catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + mModel.getUser().getToken());
                return headers;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void addPhone(String phoneNumber, final APIListener listener) {
        String url = BASE_URL + "api/phones";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("phoneNumber", phoneNumber);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mes = response.getString("message");
                        int phoneId = response.getInt("id");
                        listener.onPhoneAdded(mes, phoneId);
                    }
                    catch (JSONException e) {
                        listener.onFailed("JSON exception");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onFailed(getErrorMessage(error));
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer " + mModel.getUser().getToken());
                    return headers;
                }
            };
            mRequestQueue.add(request);
        }
        catch (JSONException e) {
            listener.onFailed("JSON exception");
        }
    }

    @Override
    public void deletePhone(int idPhone, final APIListener listener) {
        String url = BASE_URL + "api/phones/" + idPhone;

        Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mes = response.getString("message");
                    listener.onPhoneDeleted(mes);
                }
                catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + mModel.getUser().getToken());
                return headers;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void deleteAllPhones(final APIListener listener) {
        String url = BASE_URL + "api/phones" ;

        Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mes = response.getString("message");
                    listener.onAllPhonesDeleted(mes);
                }
                catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + mModel.getUser().getToken());
                return headers;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void updatePhone(int idPhone, String phoneNumber, final APIListener listener) {
        String url = BASE_URL + "api/phones/" + idPhone;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("phoneNumber", phoneNumber);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mes = response.getString("message");
                        listener.onPhoneUpdated(mes);
                    }
                    catch (JSONException e) {
                        listener.onFailed("JSON exception");
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onFailed(getErrorMessage(error));
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, successListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer " + mModel.getUser().getToken());
                    return headers;
                }
            };
            mRequestQueue.add(request);
        }
        catch (JSONException e) {
            listener.onFailed("JSON exception");
        }
    }

    @Override
    public void getDistricts(final APIListener listener) {
        String url = BASE_URL + "rajons";

        Response.Listener<JSONArray> successListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<District> districts = District.getDistricts(response);
                    if (listener != null) {
                        listener.onDistrictsLoaded(districts);
                    }
                } catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener);
        mRequestQueue.add(request);

    }

    @Override
    public void getCities(int districtId, final APIListener listener) {
        String url = BASE_URL + "rajons/" + districtId + "/nasps";

        Response.Listener<JSONArray> successListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<City> cities = City.getCities(response);
                    if (listener != null) {
                        listener.onCitiesLoaded(cities);
                    }
                } catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener);
        mRequestQueue.add(request);

    }

    @Override
    public void getStreets(int districtId, int cityId, final APIListener listener) {
        String url = BASE_URL + "rajons/" + districtId + "/nasps/" + cityId + "/streets";

        Response.Listener<JSONArray> successListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Street> streets = Street.getStreets(response);
                    if (listener != null) {
                        listener.onStreetsLoaded(streets);
                    }
                } catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener);
        mRequestQueue.add(request);

    }

    @Override
    public void getHouses(int districtId, int cityId, int streetId, final APIListener listener) {
        String url = BASE_URL + "rajons/" + districtId + "/nasps/" + cityId + "/streets/" + streetId + "/houses";

        Response.Listener<JSONArray> successListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<House> houses = House.getHouses(response);
                    if (listener != null) {
                        listener.onHousesLoaded(houses);
                    }
                } catch (JSONException e) {
                    listener.onFailed("JSON exception");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(getErrorMessage(error));
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener);
        mRequestQueue.add(request);

    }

    private String getErrorMessage(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            String jsonError = new String(networkResponse.data);
            try {
                JSONObject jsonMes = new JSONObject(jsonError);
                String mes = jsonMes.getString("message");
                return  mes;
            } catch (JSONException e) {
                return "JSON exception";
            }
        } else {
            return "Error response";
        }
    }
}
