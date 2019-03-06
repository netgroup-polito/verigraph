/**
 * @file File contains the function to populate again the restriction modal during the
 * edit configuration.
 */


/**
 * @description This function is able to populate all the fields of restriction modal with the old values.
 */
function populatedAgainRestrictionModal()
{
    var currentPolicyInt = changedPolicyInt;

    var wrapper = $(".input_fields_wrap_restrictions"); //Fields wrapper
    var funcs = NFFGcyto.policies[changedPolicyInt].policy.restrictions.functions;
    cleaningModalRestrictions();
    if(funcs.length>0)
    {
        var exactSelected = "", genericSelected = ""; // Needed to preselect the right function type

        document.getElementById("00RES").remove();

        // Preselect the right function type
        if(funcs[0].function.funcType == "generic")
          genericSelected = "selected";
        else
          exactSelected = "selected";

        $(wrapper).append
        (
            '<div id="00RES"> Select function type: ' +
            '<select id="0selectFunctionType">' +
                '<option id="0resfunction1" value="exact" ' + exactSelected + '>Exact Function</option>' +
                '<option id="0resfunction2" value="generic" ' + genericSelected + '>Generic Function</option>' +
            '</select>' +
            '<br> Name of function: <input id="0RES" class="form-control" type="text" name="mytext[]" value="' +
            funcs[0].function.funcName + '"/></div>'
        );
    }

    if(funcs.length>1)
    {
        var exactSelected, genericSelected; // Needed to preselect the right function type

        for(var i=1; i<funcs.length; i++)
        {
            restrictionsFunctionsCounter++;
            restrictionsFunctionsCounterBis++;

            exactSelected = "";
            genericSelected = "";
            // Preselect the right function type
            if(funcs[i].function.funcType == "generic")
              genericSelected = "selected";
            else
              exactSelected = "selected";

            var tempId ="sRES"  + restrictionsFunctionsCounter.toString();
            vectorIdElementRES.push(tempId);
            $(wrapper).append(  '<div id="sRES' + restrictionsFunctionsCounter.toString() +
                                  '"> Select function type: ' +
                                  '<select id="' + restrictionsFunctionsCounter.toString() + 'selectFunctionType">' +
                                      '<option id="' + restrictionsFunctionsCounter.toString() + 'resfunction1" value="exact" ' + exactSelected +
                                      '>Exact Function</option>' +
                                      '<option id="' + restrictionsFunctionsCounter.toString() + 'resfunction2" value="generic" ' + genericSelected +
                                      '>Generic Function</option>' +
                                  '</select>' +
                                  '<br> Name of function: <input id="' + restrictionsFunctionsCounter.toString() +
                                  'RES" class="form-control" type="text" name="mytext[]" value="' + funcs[i].function.funcName +
                                  '"/><a href="#" class="remove_restrictions_function">Remove</a>' +
                                '</div>');
                                //add input box
        }
    }

    changedPolicyInt = currentPolicyInt;
};
