/**
 * @file File contains all the functions to manipulate the modal for the traffic flow
 * during the creation of a policy.
 */
var restrictionsFunctionsCounter = 0; //initlal text box count
var restrictionsFunctionsCounterBis=0;
var vectorIdElementRES = [];

var idRemoveRES;

$(document).ready(function() {
    var wrapper         = $(".input_fields_wrap_restrictions"); //Fields wrapper
    var add_buttonRES      = $(".add_field_button_restrictions"); //Add button ID

    $(add_buttonRES).click(function(e)
    {
        //on add input button click
        e.preventDefault();
        restrictionsFunctionsCounter++; //text box increment
        restrictionsFunctionsCounterBis++;
        var tempId ="sRES"  + restrictionsFunctionsCounter.toString();
        vectorIdElementRES.push(tempId);
        $(wrapper).append(  '<div id="sRES' + restrictionsFunctionsCounter.toString() +
                              '"> Select function type: ' +
                              '<select id="' + restrictionsFunctionsCounter.toString() + 'selectFunctionType">' +
                                  '<option id="' + restrictionsFunctionsCounter.toString() + 'resfunction1" value="exact">Exact Function</option>' +
                                  '<option id="' + restrictionsFunctionsCounter.toString() + 'resfunction2" value="generic">Generic Function</option>' +
                              '</select>' +
                              '<br> Name of function: <input id="' + restrictionsFunctionsCounter.toString() +
                              'RES" class="form-control" type="text" name="mytext[]"/><a href="#" class="remove_restrictions_function">Remove</a>' +
                            '</div>');
                            //add input box
    });

    $(wrapper).on("click",".remove_restrictions_function", function(e)
    {
        //user click on remove text
        e.preventDefault();
        idRemoveRES =  $(this).parent('div');

        $(this).parent('div').remove();
        for(var iRES=0; iRES<restrictionsFunctionsCounter; iRES++ )
        {
           if(vectorIdElementRES[iRES].localeCompare(idRemoveRES.attr("id"))==0)
           {
               vectorIdElementRES[iRES]="-1";
           }
        }
    });

    //Get all value and save into the global variable
    $("#validateRestrictions").click(function()
    {
        var vectorRESTypes = [];
        var vectorRESNames = [];
        var countRES = 0;

        vectorRESTypes.push($("#0selectFunctionType").val());
        vectorRESNames.push($("#0RES").val());

        for(countRES=0; countRES<restrictionsFunctionsCounter; countRES++)
        {
           var tempsRES = countRES;
           if (vectorIdElementRES[tempsRES].localeCompare("-1") != 0)
           {
               tempsRES++;

               vectorRESTypes.push($("#" + tempsRES.toString() + "selectFunctionType").val());
               vectorRESNames.push($("#" + tempsRES.toString() + "RES").val());
           }
        }

        if(changedPolicyInt!=-1)
        {
           NFFGcyto.policies[changedPolicyInt].policy.restrictions.functions = [];
        }

        for(countRES=0; countRES<vectorRESTypes.length; countRES++)
        {
          // Policy template
          var functionTemplate = {
              function: {
                  funcType: vectorRESTypes[countRES],
                  funcName: vectorRESNames[countRES]
              }
          };

          if(changedPolicyInt!=-1)
          {
             NFFGcyto.policies[changedPolicyInt].policy.restrictions.functions.push(functionTemplate);
          }
          else
          {
             NFFGcyto.policies[NFFGcyto.policies.length - 1].policy.restrictions.functions.push(functionTemplate);
          }
        }

        if(changedPolicyInt!=-1)
        {
           changedPolicyInt=-1;
           alertSuccess("Modification done!");
        }
        else
        {
          setPolicyTrafficFlow();
        }

        cleaningModalRestrictions();
        change = 1;
    });

    $("#closeRestrictions").click(function()
    {
        if(changedPolicyInt==-1)
        {
           NFFGcyto.policies.pop();
        }
        cleaningModalRestrictions();
    });
 });

function cleaningModalRestrictions()
{
    changedPolicyInt=-1;
    updateOrChangeRestrictionsType=0;

    var countRES;
    for(countRES=0; countRES<restrictionsFunctionsCounter; countRES ++)
    {
       var tempsRES = countRES;
       if (vectorIdElementRES[tempsRES].localeCompare("-1") == 0);
       else {
           tempsRES++;

           var elemRES = document.getElementById("sRES" + tempsRES.toString());
           elemRES.parentNode.removeChild(elemRES);
       }
    }
    $("#0RES").val('');
    restrictionsFunctionsCounter = 0;
    restrictionsFunctionsCounterBis = 0;
    vectorIdElementRES=[];
}
