package jp.tsubakicraft.easyreport.tradeshift.controllers;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Controller
public class TokenController {

    static Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    TokenService tokenService;


    /**
     * If Access Token dos't exist in session context,
     * redirecting to authorization server to get authorization code for Access Token
     *
     * @return Return to home page
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAuthorizationCode() throws IOException {
        if (tokenService.getAccessTokenFromContext() == null) {
            LOGGER.info("redirect to the authorization server", TokenController.class);

            return "redirect:" + tokenService.getAuthorizationCodeURL();
        }
        LOGGER.info("return to index.html", TokenController.class);

        return "index.html";
    }

    /**
     * Receive authorization code from authorization server for get Access Token from authorization server
     * and redirect to the home page after that
     *
     * @param code authorization code from authorization server
     * @return Return to home page
     * @throws IOException
     */
    @RequestMapping(value = "/oauth2/code", method = RequestMethod.GET)
    public String codeResponse(@RequestParam(value = "code", required = true) String code) throws ParseException {
        LOGGER.info("get authorization token by authorization code", TokenController.class);

        OAuth2AccessToken accessToken = null;
		try {
			accessToken = tokenService.getAccessTokenByAuthCode(code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (accessToken != null) {
            LOGGER.info("succeed in to get authorization token by authorization code", TokenController.class);
        } else {
            LOGGER.warn("failed to get authorization token by authorization code", TokenController.class);
        }

        return "redirect:/";
    }

}
