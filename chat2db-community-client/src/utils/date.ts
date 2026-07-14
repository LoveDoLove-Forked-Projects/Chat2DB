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

// Calculate the user's timestamp from the timestamp in time zone 0
export function getUserTimezoneTimestamp(timestamp: number | string) {
  const timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000
  return +timestamp - timezoneOffset
}

// Given two timestamps, calculate the time difference between the two timestamps and return seconds
export function getTimestampDiff(start: number, end: number) {
  return (end - start) / 1000
}