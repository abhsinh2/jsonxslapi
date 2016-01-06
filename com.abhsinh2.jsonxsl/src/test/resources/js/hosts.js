function getHosts(hostsData, parent) {
   var hostsJson = JSON.parse(hostsData);
   var result = "<Machines>";
   for (var i in hostsJson.hosts) {
       var host = hostsJson.hosts[i];
       result = result + "<Machine>";
       result = result + "<name>" + host.host_name + "</name>";
       result = result + "<type>vm</type>";
       result = result + "<parent>" + parent + "</parent>";
       result = result + "<size>10</size>";
       result = result + "</Machine>";
   }
   result = result + "</Machines>";
   return result;
}
