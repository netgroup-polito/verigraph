/**
 * Created by Flavio Lorenzo on 07/02/19.
 */

/**
 * @file This file manage the edit policy. In particular is able to active or disable the fields after the user
 * chooses an operation. It is also able to show the right modal to edit or change the setting configuration of the node.
 */

/**
 * @description It is used in order to understand what the user wish to do:
 * 0 = nothing
 * 1 = change policy name
 * 2 = change policy type
 * 3 = change restriction
 * and to visual the right information.
 * @type {number}
 */
var changeVar = 1;

/**
* @description It manages the DOM.
*/
$(document).ready(function()
{

  /**
   * @description To add or remove the different fields.
   */
  $('#selectorEditPolicyAction').change(addOrChangePolicySelector);

  /**
   * @description This function is able to add and to remove the selector of the configuration in
   * the "Modify node" action.
   */
  function addOrChangePolicySelector()
  {
      var policyHtml= null;
      var ni = null;
      var newdiv = null;
      var divIdName = null;
      var varJquerySelector = $('#selectorEditPolicyAction').val();

      if(varJquerySelector.localeCompare("valueChangePolicyType")==0)
      {
      	if(changeVar==1)
              document.getElementById("idChangePolicyInput").remove();

      	policyHtml = ""+
            "<div id=\"idRootChangePolicyTypeSelector\">"+
	            "<select id=\"idSelectorModifyPolicy\">"+
		            "<!--The id of option is not used --> " +
					"<option id=\"p1\" value=\"selection\">Selection</option>" +
					"<option id=\"p2\" value=\"set\">Set</option>" +
					"<option id=\"p3\" value=\"sequence\">Sequence</option>" +
					"<option id=\"p4\" value=\"list\">List</option>" +
	            "</select>"+
            "</div>";

          ni = document.getElementById('rootChangePolicySelector');

          newdiv = document.createElement('div');

          divIdName = 'idRootChangePolicyTypeSelector'+'Div';

          newdiv.setAttribute('id',divIdName);

          newdiv.innerHTML = policyHtml;
          ni.appendChild(newdiv);
          changeVar=2;
      }
      else
      {
          if(varJquerySelector.localeCompare("valueChangePolicyName")==0)
          {
              if(changeVar==2)
                  document.getElementById("idRootChangePolicyTypeSelector").remove();

              policyHtml = ""+
                  "<input id=\"idChangePolicyInput\" type=\"text\" class=\"form-control input-sm\" placeholder=\"New policy ID\">";

              ni = document.getElementById('rootChangePolicySelector');

              newdiv = document.createElement('div');

              divIdName = 'idChangePolicyInput'+'Div';

              newdiv.setAttribute('id',divIdName);

              newdiv.innerHTML = policyHtml;
              ni.appendChild(newdiv);

              changeVar=1;

          }
          else
          {
              if(changeVar==1)
                  document.getElementById("idChangePolicyInput").remove();
              else
                  if(changeVar == 2)
                      document.getElementById("idRootChangePolicyTypeSelector").remove();

              changeVar=0;
          }
      }
  }
});

function modifyPolicy()
{
    var idPolicyToModify  = $("#idPolicyToModify").val();

   if(searchPolicy(idPolicyToModify)==false)
   {
       alertError("The policy doesn't exist!");
       return;
   }

   switch ($('#selectorEditPolicyAction').val())
   {
       case("valueChangePolicyName"):
       {
           changePolicyName(idPolicyToModify);
           break;
       }
       case("valueChangePolicyType"):
       {
           changePolicyType(idPolicyToModify);
           break;
       }
       case("valueChangeRestrictions"):
       {
           changeRestrictions();
           break;
       }
   }
}

function changePolicyName(oldName)
{
    var newName  = $("#idChangePolicyInput").val();

    //Check if the new name is a empty string.
    if(checkNameOfPolicy(newName)==false)
        return;

    //Chech if the new name doesn't exit on the NFFG.
    if(searchPolicy(newName)==true)
    {
        alertError("The policy already exists!");
        return;
    }

    var i = searchPolicyAndReturnIndex(oldName);

    if(i==-1)
    {
            alertError("The policy doesn't exist!");
            return;
    }

    NFFGcyto.policies[i].policy.policyName = newName;

    change=1;
    alertSuccess("Modification done!");
}

function changePolicyType(oldName)
{
    var i = searchPolicyAndReturnIndex(oldName);

    if(i==-1)
    {
            alertError("The policy doesn't exist!");
            return;
    }

    NFFGcyto.policies[i].policy.restrictions.type = $("#idSelectorModifyPolicy").val();
    alertSuccess("Modification done!");
    change=1;
}


function  changeRestrictions()
{
    //Checks if the node exist or not
    if(checksNameAndSetChangedPolicyIntVariable()==false)
        return;

    updateOrChangeRestrictionsType=0;

    cleanningModal=false;

        console.log(changedPolicyInt);
    populatedAgainRestrictionModal();

        console.log(changedPolicyInt);
    setPolicyRestrictions(NFFGcyto.policies[changedPolicyInt].policy.restrictions.type);
}

/**
 * @description This function is able to get from the DOM the name of the policy to be modified
 * and to verify if the policy exists.
 * If the policy doesn't exist, it advises the user by an alert and returns false.
 * If the policy exists, it returns true.
 * @return {boolean} - If the policy exist, it returns true otherwise false.
 */
function checksNameAndSetChangedPolicyIntVariable()
{
    console.log(changedPolicyInt);
    var oldPolicyName = $("#idPolicyToModify").val();
    changedPolicyInt = searchPolicyAndReturnIndex (oldPolicyName);

        console.log(changedPolicyInt);
    if(changedPolicyInt==-1)
    {
        alertError("The policy doesn't exist!");
        return false;
    }
    return true;
}
