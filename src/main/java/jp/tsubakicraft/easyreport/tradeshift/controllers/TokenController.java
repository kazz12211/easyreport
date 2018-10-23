package jp.tsubakicraft.easyreport.tradeshift.controllers;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import jp.tsubakicraft.easyreport.tradeshift.common.constants.TsConst;
import jp.tsubakicraft.easyreport.tradeshift.config.ApplicationProperties;
import jp.tsubakicraft.easyreport.tradeshift.services.InitialService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;
import jp.tsubakicraft.easyreport.util.OAuthUtils;

@Controller
public class TokenController {

    static Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    TokenService tokenService;

    @Autowired
    protected OAuthUtils oAuthUtils;
    
    @Autowired
    protected ApplicationProperties applicationProperties;
    
    @Autowired
    protected HttpSession session;
    
    @Autowired
    protected InitialService initialService;

    /**
     * If Access Token dos't exist in session context,
     * redirecting to authorization server to get authorization code for Access Token
     *
     * @return Return to home page
     * @throws IOException
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws JSONException 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAuthorizationCode() throws IOException, JSONException, SAXException, ParserConfigurationException {
        if(StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            session.setAttribute(TsConst.NO_RE_AUTHEN, TsConst.ACTIVE);
        }

        if (!StringUtils.equals((String)session.getAttribute(TsConst.NO_RE_AUTHEN), TsConst.ACTIVE)) {
            LOGGER.info("Logout and refresh token");
            tokenService.logout();
            return "redirect:" + oAuthUtils.getAuthorizationCodeURL();
        }
        session.setAttribute(TsConst.NO_RE_AUTHEN, null);
        oAuthUtils.checkAuthenticated();
        initialService.initUser();
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
            LOGGER.debug("cannot get access token {0}", TokenController.class);
        }
        if (accessToken != null) {
            LOGGER.info("succeed in to get authorization token by authorization code", TokenController.class);
        } else {
            LOGGER.warn("failed to get authorization token by authorization code", TokenController.class);
        }
        session.setAttribute(TsConst.NO_RE_AUTHEN, TsConst.ACTIVE);
        return "redirect:/";

    }

}
