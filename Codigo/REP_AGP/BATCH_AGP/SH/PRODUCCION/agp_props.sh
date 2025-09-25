# Reading properties file

fileManagerProp=`sed '/^\#/d' agp.batch.properties | grep "fileManager" | tail -n 1 | sed 's/^.*=//'`
userAgroseguroProp=`sed '/^\#/d' agp.batch.properties | grep "userAgroseguro" | tail -n 1 | sed 's/^.*=//'`
pwdAgroseguroProp=`sed '/^\#/d' agp.batch.properties | grep "pwdAgroseguro" | tail -n 1 | sed 's/^.*=//'`
urlUploadProp=`sed '/^\#/d' agp.batch.properties | grep "urlUpload" | tail -n 1 | sed 's/^.*=//'`
urlDownloadProp=`sed '/^\#/d' agp.batch.properties | grep "urlDownload" | tail -n 1 | sed 's/^.*=//'`
userDBProp=`sed '/^\#/d' agp.batch.properties | grep "userDB" | tail -n 1 | sed 's/^.*=//'`
pwdDBProp=`sed '/^\#/d' agp.batch.properties | grep "pwdDB" | tail -n 1 | sed 's/^.*=//'`
oracleSIDProp=`sed '/^\#/d' agp.batch.properties | grep "oracleSID" | tail -n 1 | sed 's/^.*=//'`
