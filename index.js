import {NativeModules} from 'react-native';
const datepicker = NativeModules.DatePickerAndroidNougatFix

class DatePickerAndroid{
  static async open(options: Object): Promise<Object>{
    if(options){
      const keys = ['date','minDate','maxDate'];
      keys.forEach((current)=>{
        if(
           (options[current] instanceof Date)&&
           (typeof options[current].getTime === 'function')){
          options[current] = options[current].getTime();
        }
      });
    }
    return datepicker.open(options);
  }
  static get dateSetAction() { return 'dateSetAction'; }
  static get dismissedAction() { return 'dismissedAction'; }
}

module.exports = { DatePickerAndroid };