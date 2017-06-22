/*
 The MIT License (MIT)

 Copyright (c) 2017 Nedim Cholich

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package co.frontyard.cordova.plugin.exoplayer;

import java.util.HashMap;
import android.net.*;
import android.view.ViewGroup;
import org.apache.cordova.*;
import org.json.*;
import android.util.Log;

public class Plugin extends CordovaPlugin {
    private HashMap<String, Player> players = new HashMap<String, Player>();

    @Override
    public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException {
        try {
            final Plugin self = this;
            if (action.equals("create")) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                	final String id = data.optString(0, "");
                        JSONObject params = data.optJSONObject(1);
			final Player player = new Player(new Configuration(params), cordova.getActivity(), callbackContext, webView);
			players.put(id, player);
                        player.createPlayer();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                    }
                });
                return true;
            }
            else if (action.equals("setStream")) {
                final String id = data.optString(0, "");
                final String url = data.optString(1, null);
                final JSONObject controller = data.optJSONObject(2);
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.setStream(Uri.parse(url), controller);
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });
                return true;
            }
            else if (action.equals("playPause")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.playPause();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });

                return true;
            }
            else if (action.equals("stop")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.stop();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });

                return true;
            }
            else if (action.equals("seekTo")) {
                final String id = data.optString(0, "");
                final long seekTime = data.optLong(1, 0);
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.seekTo(seekTime);
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });
                return true;
            }
            else if (action.equals("getState")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        JSONObject response = player.getPlayerState();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, response, false);
                    }
                });
                return true;
            }
            else if (action.equals("showController")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.showController();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });
                return true;
            }
            else if (action.equals("hideController")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.hideController();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                    }
                });
                return true;
            }
            else if (action.equals("close")) {
                final String id = data.optString(0, "");
		final Player player = players.get(id);
                if (player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        player.close();
			players.remove(id);
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, false);
                    }
                });
                return true;
            }
            else {
                new CallbackResponse(callbackContext).send(PluginResult.Status.INVALID_ACTION, false);
                return false;
            }
        }
        catch (Exception e) {
            new CallbackResponse(callbackContext).send(PluginResult.Status.JSON_EXCEPTION, false);
            return false;
        }
    }
}
