export function cleanUpEmpty(object: Record<any, any> | any[], parentKey?: any): void{
    if(!object){
        return;
    }

    if(Array.isArray(object)){
        if(!parentKey){
            throw'missing parent key';
        }
        return;

    }



    Object.keys(object).forEach(key =>{

        const targetValue = object[key];

        if((targetValue === null) || (targetValue === undefined)){
            delete object[key];
            return;
        }

        if(Array.isArray(targetValue)){
            if(!targetValue?.length){
                delete object[key];
                return;
            }
            targetValue.forEach(obj => cleanUpEmpty(obj, key));
            return;
        }

        if(targetValue instanceof Object){
            cleanUpEmpty(targetValue);
            if(!Object.keys(targetValue).length){
                delete object[key];
                return;
            }
        }

    })
}