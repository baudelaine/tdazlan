$(document).ready(function() {

    $("#EnvoyerMessage").removeClass("disabled");

});

// $('#message').maxlength({
//     alwaysShow: true,
//     threshold: 150,
//     warningClass: "label label-warning",
//     limitReachedClass: "label label-danger"
// });

function EffacerMessage(){

    $("#message").val("");
}

$('#message').keyup(function () {
    
    var max = 160;
    var len = $(this).val().length;
    var char = max - len;
    var percent = (len / 160) * 100;
    $('#charNum').text(char + ' caractère(s) restant(s)');

    $('.progress-bar').css('width', percent + '%');

    if (percent <= 75) {
        $('.progress-bar').removeClass().addClass("progress-bar progress-bar-success progress-bar-striped active");        
    }

    if (percent > 75) {
        $('.progress-bar').removeClass().addClass("progress-bar progress-bar-warning progress-bar-striped active");        
    }

    if (percent > 90) {
        $('.progress-bar').removeClass().addClass("progress-bar progress-bar-danger progress-bar-striped active");        
    }
    if (percent == 100) {
        $('.progress-bar').removeClass().addClass("progress-bar progress-bar-danger");        
    }

});


$(document)
.ajaxStart(function(){
    //$("#ajaxSpinnerImage").show();
    $("div#divLoading").addClass('show');
})

.ajaxStop(function(){
    //$("#ajaxSpinnerImage").hide();
    $("div#divLoading").removeClass('show');
});

function showalert(message, alerttype) {

    $('#alert_placeholder').append('<div id="alertdiv" class="alert ' + 
		alerttype + ' input-sm"><span>' + message + '</span></div>')

    setTimeout(function() {

      $("#alertdiv").remove();

    }, 2500);
}

function contactsTable(){

    var cols = [];
    cols.push({checkbox: "true"});
    cols.push({field:"prenom", title: "Prénom" });
    cols.push({field:"nom", title: "Nom" });
    cols.push({field:"societe", title: "Société" });
    cols.push({field:"email", title: "Email" });
    cols.push({field:"telephone", title: "Téléphone" });

    $('#contacts').bootstrapTable("destroy").bootstrapTable({
        columns: cols
    });
    
}

function messagesTable(){

    var cols = [];
    cols.push({field:"from", title: "Auteur" });
    cols.push({field:"to", title: "Destinataire" });
    cols.push({field:"hwhen", title: "Date" });
    cols.push({field:"sid", title: "Sid" });
    cols.push({field:"status", title: "Status" });
    cols.push({field:"body", title: "Message"});

    $('#contacts').bootstrapTable("destroy").bootstrapTable({
        columns: cols
    });
    
}

function EnvoyerMessage(){

    if ($("#message").val() == "") {
        showalert("Saisir un message.", "alert-warning");
        return;
    }

    selections = $('#contacts').bootstrapTable('getSelections');
    
    console.log("selections=" + selections);
    if (selections == "") {
        showalert("Aucun contact selectionné.", "alert-warning");
        return;
    }
    
    
    hastelephone = true;

    $.each(selections, function(k, v){
        if (v.telephone == "") {
            hastelephone = false;
        }
    });

    if (hastelephone == false) {
        showalert("Un des contact(s) selectionné(s) n'a pas de téléphone.", "alert-warning");
        return;
    }
    
    var contacts = new Object();
    $.each(selections, function(k, v){
        var contact = new Object();
        contact.message = $("#message").val();
        contact.telephone = v.telephone;
        contacts[v.prenom + "." + v.nom] = contact;
    });

    console.log("contacts[0]=" + contacts[0]);
    console.log("contacts=" + contacts);
    console.log("contacts=" + JSON.stringify(contacts));

    $.ajax({
        type: 'POST',
        url: "EnvoyerMessage",
        dataType: 'json',
        data: JSON.stringify(contacts),

        success: function(data) {
            console.log(data);
            messagesTable();

            if(data.errorMessage == "Authenticate" && data.errorCode == "20003"){
                showalert("Erreur d'authentification TWILIO.", "alert-danger");
                return;
            }
            else {
                $('#contacts').bootstrapTable("load", data);
                showalert("Message(s) envoyé(s) avec succès.", "alert-success");
            }
        },
        error: function(data) {
            console.log(data);
            showalert("Erreur lors de l'envoi de(s) message(s).", "alert-danger");
        }        
        
    });    

    $("#EnvoyerMessage").addClass("disabled");


}

// !!!!!!!!!!!! MANDATORY TO DISPLAY DATA IN TABLE !!!!!!!!!!!!!!
$('#contacts').bootstrapTable({});

function RecupererContacts() {

    $.ajax({
        type: 'POST',
        url: "RecupererContact",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            contactsTable();
            $('#contacts').bootstrapTable("load", data);
          },
        error: function(data) {
            console.log(data);
            showalert("Erreur lors de la récupération de(s) contact(s).", "alert-danger");
        }        
        
    });
    
    $("#EnvoyerMessage").removeClass("disabled");
}


function EnvoyerContact(){

	if ($("#prenom").val() == "") {
		showalert("Saisir un prénom.", "alert-warning");
		return;
	}

	if ($("#nom").val() == "") {
		showalert("Saisir un nom.", "alert-warning");
		return;
	}

	var contact = new Object();
    contact._id = $("#prenom").val() + "." + $("#nom").val();
    contact._rev = null;
	contact.prenom = $("#prenom").val();
	contact.nom = $("#nom").val();
	contact.societe = $("#societe").val();
	contact.email = $("#email").val();
	contact.telephone = $("#telephone").val();

	console.log("contact=" + contact);

	$.ajax({
        type: 'POST',
        url: "EnvoyerContact",
        dataType: 'json',
        data: JSON.stringify(contact),        

        success: function(data) {
            var conflict = false;
            console.log(data);
            $.each(data, function(k, v){
                console.log(v);
                if (v == "conflict"){
                    conflict = true;                    
                }
            });
            if (conflict == true) {
                showalert("Prénom.Nom dupliqué.", "alert-warning");
                return;
            }
            $('#contacts').bootstrapTable("load", data);
    	    showalert("Contact envoyé avec succès.", "alert-success");
        },
        error: function(data) {
            console.error(data);
            showalert("Erreur lors de l'envoi du contact.", "alert-danger");
        }        
        
	});

}


function Reset() {
	
	var success = "OK";
	
	$.ajax({
        type: 'POST',
        url: "Logout",
        dataType: 'json',

        success: function(data) {
			success = "OK";
        },
        error: function(data) {
            console.log(data);
   			success = "KO";
        }        
        
    });	

	if (success == "KO") {
		showalert("Reset() failed.", "alert-danger");
	}

	location.reload(true);
	
}

/**
 * 
 */

var contacts = [];

function reset(){
    
    location.reload();
    
}

function UploadCSVFile(){
    
    console.log("submit event");
    var fd = new FormData(document.getElementById("fileinfo"));
    $.ajax({
      url: "UploadCSVFile",
      type: "POST",
      data: fd,
      enctype: 'multipart/form-data',
      dataType: 'json',
      processData: false,  // tell jQuery not to process the data
      contentType: false   // tell jQuery not to set contentType
    }).done(function( data ) {
        contacts = data;
        console.log(contacts);
        $("#historique tr").remove();
        $("#historique").append("<tr><th>Name</th><th>Email</th><th>Phone</th></tr>");
        drawContacts(contacts);        
    });

    return false;
}

function GetEventMessage() {

    $.ajax({
        type: 'POST',
        url: "GetEventMessage",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            $("#message").val(data);
          },
        error: function(data) {
            console.log(data);
        }        
        
    });
    
}

function GetSampleMessage() {

    $.ajax({
        type: 'POST',
        url: "GetSampleMessage",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            $("#message").val(data);
          },
        error: function(data) {
            console.log(data);
        }        
        
    });
    
}

function GetEventContacts() {

    $.ajax({
        type: 'POST',
        url: "GetEventContacts",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            contacts = data;
            $("#historique tr").remove();
            $("#historique").append("<tr><th>Name</th><th>Email</th><th>Phone</th></tr>");
            drawContacts(data);
          },
        error: function(data) {
            console.log(data);
        }        
        
    });
    
}

function SendSMS() {
 
    
    console.log(contacts);
    // get inputs
    var parms = new Object();
    parms.message = $('#message').val();
    parms.contacts = contacts;
    parms.gsms = $('#gsms').val().split(',');
    
    $.ajax({
        type: 'POST',
        url: "SendSMS",
        dataType: 'json',
        data: JSON.stringify(parms),        

        success: function(data) {
            console.log(data);
            $("#historique tr").remove();
            $("#historique").append("<tr><th>Sid</th><th>When</th><th>From</th><th>To</th><th>Status</th><th>About</th></tr>");
            drawTable(data);
          },
        error: function(data) {
            console.log(data);
        }        
        
    });
}

function drawContacts(data) {
    
    var gsms = "";
    for (var i = 0; i < data.length; i++) {
        drawContact(data[i]);
        gsms += data[i].gsm + ",";
    }
    console.log(gsms);
    $('#gsms').val(gsms);
    
}

function drawContact(rowData) {
    var row = $("<tr />");
    $("#historique").append(row);
    row.append($("<td>" + rowData.nom + " " + rowData.prenom + "</td>"));
    row.append($("<td>" + rowData.email + "</td>"));
    row.append($("<td>" + rowData.gsm + "</td>"));
}


function drawTable(data) {
    
    for (var i = 0; i < data.length; i++) {
        drawRow(data[i]);
    }
    
}

function drawRow(rowData) {
    var row = $("<tr />");
    $("#historique").append(row);
    row.append($("<td>" + rowData.sid + "</td>"));
    row.append($("<td>" + rowData.hwhen + "</td>"));
    row.append($("<td>" + rowData.from + "</td>"));
    row.append($("<td>" + rowData.to + "</td>"));
    row.append($("<td>" + rowData.status + "</td>"));
    row.append($("<td>" + rowData.body + "</td>"));
}

$(document).on('change', '.btn-file :file', function() {
      var input = $(this),
          numFiles = input.get(0).files ? input.get(0).files.length : 1,
          label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
      input.trigger('fileselect', [numFiles, label]);
      console.log("numFiles=" + numFiles);
      console.log("label=" + label);
      $('#infos').text(label + " has been successfully loaded and is now ready to be uploaded.");
});

