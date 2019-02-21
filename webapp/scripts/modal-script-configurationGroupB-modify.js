/**
 * @file File contains the function for populate again the firewall  modal during the
 * edit configuration.
 */


/**
 * @description This function is able to populate all the fields of modal B with the old name of the configuration
 * of the node X.
 * The function before gets the "point id" of "input_fields_wrap_configurationGroupA" id into wrapper variable and
 * it copies the configuration into a variable (conf variable). After that, it checks if the length is more thant
 * zero in order to delete the first field of the modal and to put another (with the same id) but in addition with
 * the name of the configuration into the text field.
 * After this operation, it continues in this way (more or less) but before it checks if the length is more than one
 * and if it is more than than one, it creates new field similar to modal A js file. In this way the two files
 * (this, and modal-script-configurationGroupB.js) are able to work together because in the DOM there are the some
 * id for the field. Consequently when the user remove a field, the action is done from the other file. This file is
 * only able to populate the modal with the old field.
 */


    function  populatedAgainModalB() {
    var wrapper = $(".input_fields_wrap_configurationGroupB"); //Fields wrapper
    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    var sourceNodeFirewall = "";
    var destinationNodeFirewall = "";
    var sourcePortFirewall = "";
    var destinationPortFirewall = "";

    rootCleanningModalModifyNode("B");

    for(var i = 0; i<conf.length; i++)
    {
        if (i == 0)
        {
            document.getElementById("00B").remove();

            sourceNodeFirewall = conf[i].source_id;
            destinationNodeFirewall = conf[i].destination_id;
            sourcePortFirewall = conf[i].source_port;
            destinationPortFirewall = conf[i].destination_port;

            var tcpSelected = "", udpSelected = ""; // Needed to preselect the right function type
            // Preselect the right function type
            if(conf[i].protocol == "TCP")
              tcpSelected = "selected";
            else
              udpSelected = "selected";

            $(wrapper).append
            (
                "<div id=\"00B\">" +
                "Source existing node" +
                "<input class=\"form-control\" id=\"0B\" type=\"text\" name=\"mytext[]\" value='" + sourceNodeFirewall + "'> <br>" +
                "Destination existing source" +
                "<input class=\"form-control\" id=\"0BB\" type=\"text\" name=\"mytext[]\" value='" + destinationNodeFirewall + "'> <br>" +
                "Source port" +
                "<input class=\"form-control\" id=\"0BC\" type=\"text\" name=\"mytext[]\" value='" + sourcePortFirewall + "'> <br>" +
                "Destination port" +
                "<input class=\"form-control\" id=\"0BD\" type=\"text\" name=\"mytext[]\" value='" + destinationPortFirewall + "'> <br>" +
                "Value of protocol: <select id=\"0BE\">" +
                  "<option value=\"TCP\" " + tcpSelected + ">TCP PROTOCOL</option>" +
                  "<option value=\"UDP\" " + udpSelected + ">UDP PROTOCOL</option>" +
                "</select>" +
                "</div>"
            );
        }
        else {
          sourceNodeFirewall = conf[i].source_id;
          destinationNodeFirewall = conf[i].destination_id;
          sourcePortFirewall = conf[i].source_port;
          destinationPortFirewall = conf[i].destination_port;

          var tcpSelected = "", udpSelected = ""; // Needed to preselect the right function type
          // Preselect the right function type
          if(conf[i].protocol == "TCP")
            tcpSelected = "selected";
          else
            udpSelected = "selected";

            configurationGroupAcounterB++; //text box increment
            configurationGroupAcounterBisB++;
            var tempId = "sB" + configurationGroupAcounterB.toString();
            vectorIdElementB.push(tempId);

            $(wrapper).append(
                '<div id="sB'+ configurationGroupAcounterB.toString()
                + '"><br>Source existing node <input id="' + configurationGroupAcounterB.toString()
                + 'B"class="form-control" type="text" name="mytext[]" ' + sourceNodeFirewall + '/>' +
                '<br>Destination existing source <input id="' + configurationGroupAcounterB.toString() + 'BB"class="form-control" type="text" name="mytext[]" ' + destinationNodeFirewall + '/>' +
                '<br />Source port <input id="' + configurationGroupAcounterB.toString() + 'BC"class="form-control" type="text" name="mytext[]" ' + sourcePortFirewall + '/>' +
                '<br />Destination port <input id="' + configurationGroupAcounterB.toString() + 'BD"class="form-control" type="text" name="mytext[]" ' + + destinationPortFirewall + + '/>' +
                '<br />Value of protocol: ' +
                '<select id="' + configurationGroupAcounterB.toString() + 'BE">' +
                    '<option value="TCP" ' + tcpSelected + '>TCP PROTOCOL</option>' +
                    '<option value="UDP" ' + tcpSelected + '>UDP PROTOCOL</option>' +
                '</select>' +
                '<a href="#" class="remove_field">Remove</a></div>');
        }
        i++;
    }


}
