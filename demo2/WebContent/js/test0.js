$(document).ready(function(){
    $("div.content").click(function(){
        $("div#divLoading").addClass('show');
    });
});

function loadTable(){

    var cols = [];
    cols.push({field:"body", title: "Message" });
    cols.push({field:"from", title: "Auteur" });
    cols.push({field:"to", title: "Destinataire" });
    cols.push({field:"hwhen", title: "Date" });
    cols.push({field:"sid", title: "Sid" });
    cols.push({field:"status", title: "Status" });

    $('#table').bootstrapTable("destroy").bootstrapTable({
        columns: cols
    });

    console.log("attr=" + $("#test0").attr("disabled"));
    $("#test0").removeClass("active");
    console.log("attr=" + $("#test0").attr("disabled"));
    
}