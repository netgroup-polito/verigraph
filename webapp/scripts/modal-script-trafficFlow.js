/**
 * @file File contains all the functions to manipulate the modal for the traffic flow
 * during the creation of a policy.
 */
 $(document).ready(function() {

     //Get the value from the modal
     $("#validateConfigurationTrafficFlow").click(function()
     {
         //Create a local variable and to save the value inside
         var jsonVariable = {};

         //HTTP body
         if ($('#TF1CB').is(":checked"))
         {
             // it is checked
             jsonVariable["body"] = $("#TF1").val();
         }

         //Sequence number
         if ($('#TF2CB').is(":checked"))
         {
             // it is checked
             var s = parseInt($("#TF2").val());

             if(isNaN(parseFloat(($("#TF2").val()))))
             {
                 alertError("Your sequence number is not of sequence of number!");
                 return false;
             }
             jsonVariable["sequence"] = parseInt($("#TF2").val());
         }

         //Protocol
         if ($('#TF3CB').is(":checked"))
         {
             // it is checked
             jsonVariable["protocol"] = $("#TF3").val();
         }

         //E-mail sender
         if ($('#TF4CB').is(":checked"))
         {
             // it is checked
             jsonVariable["email_from"] = $("#TF4").val();
         }

         //URL
         if ($('#TF5CB').is(":checked"))
         {
             // it is checked
             jsonVariable["url"] = $("#TF5").val();
         }

         //Options
         if ($('#TF6CB').is(":checked"))
         {
             // it is checked
             jsonVariable["options"] = $("#TF6").val();
         }

         //Destination node
         if ($('#TF7CB').is(":checked"))
         {
             // it is checked
             jsonVariable["destination"] = $("#TF7").val();
         }

         if(changedPolicyInt!=-1)
         {
             NFFGcyto.policies[changedPolicyInt].policy.trafficFlow = [];
             NFFGcyto.policies[changedPolicyInt].policy.trafficFlow.push(jsonVariable);
             changedPolicyInt=-1;
             alertSuccess("Modification done!");
         }
         else
         {
             //Save also the configuragion.
             NFFGcyto.policies[NFFGcyto.policies.length - 1].policy.trafficFlow.push(jsonVariable);
         }
         cleaningModalTrafficFlow();
         change = 1;
     });

     $("#closeConfigurationTrafficFlow").click(function()
     {
         if(changedPolicyInt==-1)
         {
            NFFGcyto.policies.pop();
         }
         cleaningModalTrafficFlow();
     });
 });

 function cleaningModalTrafficFlow()
 {
     changedPolicyInt=-1;

     //HTTP body
     if ($('#TF1CB').is(":checked"))
     {
         $('#TF1CB').removeAttr('disabled').prop('checked', false);

         $("#TF1").attr("disabled", "disabled");
         var ta = document.getElementById('TF1');
         ta.value = "";

     }

     //Sequence number
     if ($('#TF2CB').is(":checked"))
     {
         $('#TF2CB').removeAttr('disabled').prop('checked', false);

         $('#TF2').attr("disabled", "disabled");
         var ta = document.getElementById('TF2');
         ta.value = "";
     }

     //Protocol
     if ($('#TF3CB').is(":checked"))
     {
         $('#TF3CB').removeAttr('disabled').prop('checked', false);

         $('#TF3').attr("disabled", "disabled");
         var ta = document.getElementById('TF3');
         ta.value = "";
     }

     //E-mail sender
     if ($('#TF4CB').is(":checked"))
     {
         $('#TF4CB').removeAttr('disabled').prop('checked', false);

         $('#TF4').attr("disabled", "disabled");
         var ta = document.getElementById('TF4');
         ta.value = "";
     }

     //URL
     if ($('#TF5CB').is(":checked"))
     {
         $('#TF5CB').removeAttr('disabled').prop('checked', false);

         $('#TF5').attr("disabled", "disabled");
         var ta = document.getElementById('TF5');
         ta.value = "";
     }

     //Options
     if ($('#TF6CB').is(":checked"))
     {
         $('#TF6CB').removeAttr('disabled').prop('checked', false);

         $('#TF6').attr("disabled", "disabled");
         var ta = document.getElementById('TF6');
         ta.value = "";
     }

     //Destination node
     if ($('#TF7CB').is(":checked"))
     {
         $('#TF7CB').removeAttr('disabled').prop('checked', false);

         $('#TF7').attr("disabled", "disabled");
         var ta = document.getElementById('TF7');
         ta.value = "";
     }
 }
