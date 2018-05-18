package com.rmadegps.rmadeservermanagement.controller;



import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class api1 {

    rmade_server_management rmade_server_management_data_connection = new rmade_server_management();
    DataSource rmade_server_management = rmade_server_management_data_connection.setUp();

   // public

    public api1() throws Exception
    { }


    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model)
    {
        return "dashboard";
    }

    @RequestMapping(value = "/pid_services", method = RequestMethod.GET)
    public String pid_services(Model model) {
        return "pid_services";
    }

    @RequestMapping(value = "/calender", method = RequestMethod.GET)
    public String calender(Model model) {
        return "calender";
    }

    @RequestMapping(value = "/loadtop", method = RequestMethod.GET)
    public String loadtop(Model model) {
        return "loadtop";
    }

    @RequestMapping(value = "/loadmap", method = RequestMethod.GET)
    public String loadmap(Model model) {
        return "loadmap";
    }

    @RequestMapping(value = "/loaddetailconfig", method = RequestMethod.GET)
    public String loaddetailconfig(Model model) {
        return "loaddetailconfig";
    }

    @RequestMapping(value = "/highchart", method = RequestMethod.GET)
    public String highchart(Model model) {
        return "highchart";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {

        return "login";
    }

    @RequestMapping(value = "/serverload", method = RequestMethod.GET)
    public String serverload(Model model) {

        return "serverload";
    }


    @RequestMapping(value = "/deviceconfig", method = RequestMethod.GET)
    public String deviceconfig(Model model) {

        return "deviceconfig";
    }


    @RequestMapping(value = "/nohup_out", method = RequestMethod.GET)
    public String nohup_out(Model model) {

        return "nohup_out";
    }


    @RequestMapping(value = "/pid_add_edit", method = RequestMethod.GET)
    public String pid_add_edit(Model model) {

        return "pid_add_edit";
    }


    @RequestMapping(value = "/device_add_edit", method = RequestMethod.GET)
    public String device_add_edit(Model model) {

        return "device_add_edit";
    }

    //Save the uploaded file to this folder
   // private static String UPLOADED_FOLDER = "/home/ZNetcloudssh/tester/";



    @RequestMapping(path = "/upload", method = RequestMethod.GET)
    public String index(Model model)
    {
        return "upload";
    }


    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String singleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam String pid_id, RedirectAttributes redirectAttributes,Model model) throws SQLException
    {
        String UPLOADED_FOLDER = "/home/rmade_server/nohup_out/images/";

        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt = rmade_server_management_con.createStatement();

        if (file.isEmpty())
        {
           // redirectAttributes.addFlashAttribute( "message", "Please select a file to upload" );
            //return "redirect:/uploadStatus";.
            return "pid_add_edit";

        }

        try
        {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get( UPLOADED_FOLDER + file.getOriginalFilename() );
            Files.write( path, bytes );


           String sql1 = "update rpt_server_jars set image_path='" + String.valueOf(path) + "' where pid_id='" + pid_id + "'";
           //rmade_server_management_stmt.executeUpdate( sql1 );
            rmade_server_management_con.close();

     /*       redirectAttributes.addFlashAttribute( "message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'" );*/

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "pid_add_edit";
    }


}