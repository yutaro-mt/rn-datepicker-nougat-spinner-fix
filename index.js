import {NativeModules} from 'react-native';

const datepicker = NativeModules.DatePickerAndroidNougatFix

class DatePickerAndroid{
  static async open(options: Object): Promise<Object>{
    if(options){
      const keys = ['date','minDate','maxDate'];
      keys.forEach((current)=>{
        if(( typeof options[current] === 'object' )&&
           ( options[current] instanceof Date)){
          options[current] = options[current].getTime();
        }
      });
    }
    return datepicker.open(options);
  }
}

module.exports = DatePickerAndroid;