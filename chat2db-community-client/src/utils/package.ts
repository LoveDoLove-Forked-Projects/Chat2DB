// @/xxxx cannot be recognized in .umirc, so webpack.ts is opened separately. This file can only write functions and cannot introduce other components.

// Separate the parameters added in the yarn startup command
export function extractYarnConfig(argv: string[]){
  const newArgv = argv.slice(2)
  const yarn_config:{[k in string]: string} = {}
  newArgv.forEach(t=>{
    if(t && t.startsWith("--")){
      const regex = /--(.+?)=(.+)/;
      const matches = t.match(regex);
      if (matches) {
        const key = matches[1];
        const value = matches[2];
        yarn_config[key] = value
      }
    }
  })
  return yarn_config
}

export function formatDate(date: any, fmt = 'yyyy-MM-dd') {
  if (!date) {
    return '';
  }
  if (typeof date == 'number' || typeof date == 'string') {
    date = new Date(date);
  }
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }
  var o: any = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    S: date.getMilliseconds(),
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
}


export function generateBuildTime() {
  // Get current server time
  const serverTime = new Date();

  // Get the time difference (minutes) of the server time zone
  const serverOffset = serverTime.getTimezoneOffset() * 60000;

  // Convert server time to UTC time
  const utcTime = serverTime.getTime() + serverOffset;

  // Time difference of Beijing time (UTC+8)
  const beijingOffset = 8 * 60 * 60000;

  // Convert UTC time to Beijing time
  const beijingTime = new Date(utcTime + beijingOffset);

  // Format time
  return formatDate(beijingTime, 'yyyy-MM-dd hh:mm:ss');
}