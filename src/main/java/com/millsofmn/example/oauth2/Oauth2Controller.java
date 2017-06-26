package com.millsofmn.example.oauth2;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by M108491 on 4/6/2017.
 */
@RestController
public class Oauth2Controller {

    // can be see by only authenticated users
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    // can be see by authenticated and unauthenticated users
    @RequestMapping("/about")
    public String about() {
        return "I am text in the about page.";
    }

    // https://localhost:8443/sia/link/here/to/there?repeat=5
    @RequestMapping("/link/{here}/to/{there}")
    public String deepLinking(@PathVariable String here, @PathVariable String there, @RequestParam("repeat") Integer repeat){

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < repeat; i++){
            sb.append(i+1).append(". Deep linking '").append(here).append("' to '").append(there).append("'<br>");
        }

        return sb.toString();
    }

}
